package com.feastora.food_ordering.service;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.config.ServerConfig;
import com.feastora.food_ordering.entity.User;
import com.feastora.food_ordering.entity.VerificationToken;
import com.feastora.food_ordering.enums.VerificationEnum;
import com.feastora.food_ordering.event.RegistrationControllerEvent;
import com.feastora.food_ordering.model.UserModel;
import com.feastora.food_ordering.repository.UserRepository;
import com.feastora.food_ordering.repository.VerificationTokenRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;

@Service
public class UserService extends BaseResponse {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher publisher;
    private final ServerConfig serverConfig;
    private static final int EXPIRATION_DAYS = 1;
    private final JwtUtil jwtUtil;
    private final ServletRequest httpServletRequest;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, ApplicationEventPublisher publisher, ServerConfig serverConfig, JwtUtil jwtUtil, ServletRequest httpServletRequest) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.publisher = publisher;
        this.serverConfig = serverConfig;
        this.jwtUtil = jwtUtil;
        this.httpServletRequest = httpServletRequest;
    }


    public GenericResponse<String> registerUser(UserModel userModel, final HttpServletRequest request) {
        User userByMobileNum = userRepository.findUserByUserNameAndMobileNum(userModel.getUserName(), userModel.getMobileNum());
        if (userByMobileNum != null) {
            return newRestErrorResponse(409, "User with this mobile Number and username already exists");
        }
        String token = jwtUtil.generateTokenForUserModel(userModel);
        publisher.publishEvent(new RegistrationControllerEvent(
                token, serverConfig.applicationUrl(request)
        ));
        return newRestResponseData(String.format("Congrats, %s registered successfully !!!",
                Arrays.stream(userModel.getUserName().split("\\s")).toArray()[0]));
    }

    public void saveVerificationTokenForUser(String userId, String token) {
        verificationTokenRepository.save(create(userId, token));
    }

    public VerificationEnum verifyVerificationToken(String token) {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findVerificationTokenByToken(token);
            if (verificationToken == null) {
                return VerificationEnum.INVALID_TOKEN;
            }
            String userId = verificationToken.getUserId();
            userRepository.enableUserByUserId(userId);
            return VerificationEnum.VALID_TOKEN;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return VerificationEnum.EXPIRED_TOKEN;
        }
    }

    public GenericResponse<String> getLoginDetails(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return newRestErrorResponse(400, "UserName/ password is mismatch", "check username/password");
        }
        User user = userRepository.findUserByUserName(username);
        if (user == null) {
            return newRestErrorResponse(404, "User not found", "Register User");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return newRestErrorResponse(400, "Password does not match", "check password");
        }
        httpServletRequest.setAttribute("user", user);
        return newRestResponseData("Welcome," + user.getUserName() + " Successfully logged in");
    }

    public static VerificationToken create(String userId, String token) {
        long now = System.currentTimeMillis();
        long expiry = now + EXPIRATION_DAYS * 24 * 60 * 60 * 1000L;
        return VerificationToken.builder()
                .userId(userId)
                .token(token)
                .createdAtEpoch(now)
                .expiresAtEpoch(expiry)
                .expiresAt(new Date(expiry))
                .build();
    }

    public void saveUserEntity(UserModel userModel) {
        User user = new User();
        user.setUserId(userModel.getUserId());
        user.setUserName(userModel.getUserName());
        user.setEmail(userModel.getEmail());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole(userModel.getRole());
        user.setMobileNum(userModel.getMobileNum());
        user.setLastModified(System.currentTimeMillis());
        user.setDateCreated(System.currentTimeMillis());
        userRepository.save(user);
    }
}
