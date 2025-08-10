package com.feastora.food_ordering.service;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.config.ServerConfig;
import com.feastora.food_ordering.entity.User;
import com.feastora.food_ordering.entity.VerificationToken;
import com.feastora.food_ordering.enums.Role;
import com.feastora.food_ordering.enums.VerificationEnum;
import com.feastora.food_ordering.event.RegistrationControllerEvent;
import com.feastora.food_ordering.mapping.MapperUtils;
import com.feastora.food_ordering.model.UserModel;
import com.feastora.food_ordering.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService extends BaseResponse {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;
    private final ServerConfig serverConfig;
    private final JwtUtil jwtUtil;
    private final VerificationTokenService verificationTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher publisher, ServerConfig serverConfig, JwtUtil jwtUtil, VerificationTokenService verificationTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.publisher = publisher;
        this.serverConfig = serverConfig;
        this.jwtUtil = jwtUtil;
        this.verificationTokenService = verificationTokenService;
    }


    public GenericResponse<String> registerUser(UserModel userModel, final HttpServletRequest request) {
        Optional<User> users = userRepository.getUserByEmail(userModel.getEmail());
        User userEmailExists = users.orElse(null);
        if (ObjectUtils.isNotEmpty(userEmailExists)) {
            return newRestErrorResponse(409, "User with this email already exists");
        }
        String token = jwtUtil.generateTokenForUserModel(userModel);
        publisher.publishEvent(new RegistrationControllerEvent(
                token, userModel.getUserName(), userModel.getEmail(), serverConfig.applicationUrl(request)
        ));
        return newRestResponseData("Registered");
    }

    public void saveVerificationTokenForUser(String token, User user) {
        Date expiry = jwtUtil.extractExpiration(token);
        long now = System.currentTimeMillis();
        VerificationToken verificationToken = VerificationToken.builder()
                .userId(user.get_id())
                .token(token)
                .createdAtEpoch(now)
                .expiresAtEpoch(expiry.getTime())
                .expiresAt(expiry)
                .build();
        verificationTokenService.saveVerificationToken(verificationToken);
    }

    public VerificationEnum verifyVerificationToken(String token) {
        try {
            Claims claims = jwtUtil.validateToken(token);
            if (ObjectUtils.isEmpty(claims) || jwtUtil.isTokenExpired(token)) {
                return VerificationEnum.EXPIRED_TOKEN;
            }
            UserModel userModel = jwtUtil.getUserModelFromToken(token);
           User user = saveUserEntity(userModel);
            saveVerificationTokenForUser(token, user);
            return VerificationEnum.VALID_TOKEN;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return VerificationEnum.EXPIRED_TOKEN;
        }
    }

    public GenericResponse<String> getLoginDetails(UserModel userModel) {
        if (ObjectUtils.isEmpty(userModel) || StringUtils.isAnyBlank(userModel.getEmail(), userModel.getPassword())) {
            return newRestErrorResponse(400, "Email/ password is mismatch", "check username/password");
        }
        try {
            String email = userModel.getEmail();
            String password = userModel.getPassword();
            Optional<User> users = userRepository.getUserByEmail(email);
            User user = users.orElse(null);
            if (ObjectUtils.isEmpty(user)) {
                return newRestErrorResponse(404, "User not found", "Email Not Found");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return newRestErrorResponse(400, "Password does not match", "check password");
            }

            VerificationToken verifiedUser = verificationTokenService.getVerificationTokenByUserId(user.get_id());
            if (verifiedUser == null) {
                return newRestErrorResponse(404, "User is not Verified", "Please register first");
            }
            String token = verifiedUser.getToken();
            UserModel newUser = MapperUtils.convertObjectValueToResponseObject(user, UserModel.class);
            if (ObjectUtils.isEmpty(token) || ObjectUtils.isEmpty(newUser)) {
                return newRestErrorResponse(400, "Token is empty", "check token");
            }
            if (jwtUtil.isTokenExpired(token)) {
                token = jwtUtil.generateTokenForUserModel(newUser);
                verifiedUser.setToken(token);
                verificationTokenService.updateVerificationTokenForUserById(verifiedUser.getUserId(), verifiedUser);
            }
            return newRestResponseData("Welcome," + user.getUserName() + " Successfully logged in");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return newRestErrorResponse(400, "Error", "check email/password");
        }
    }

    public User saveUserEntity(UserModel userModel) {
        User user = new User();
        user.setUserName(userModel.getUserName());
        user.setEmail(userModel.getEmail());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole(Role.valueOf(userModel.getRole()));
        user.setMobileNum(userModel.getMobileNum());
        user.setLastModified(System.currentTimeMillis());
        user.setDateCreated(System.currentTimeMillis());
        user.setEnabled(true);
       return userRepository.save(user);
    }
}
