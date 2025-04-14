package com.feastora.food_ordering.service;

import com.feastora.food_ordering.Errorhandling.ResponseError;
import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.entity.User;
import com.feastora.food_ordering.entity.VerificationToken;
import com.feastora.food_ordering.enums.VerificationEnum;
import com.feastora.food_ordering.event.RegistrationControllerEvent;
import com.feastora.food_ordering.model.UserModel;
import com.feastora.food_ordering.repository.UserRepository;
import com.feastora.food_ordering.repository.VerificationTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserService extends BaseResponse {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private ApplicationEventPublisher publisher;


    public GenericResponse<String> registerUser(UserModel userModel, final HttpServletRequest request, long tableNumber) {
        User user = new User();
        user.setUserId(userModel.getUserId());
        user.setUserName(userModel.getUserName());
        user.setEmail(userModel.getEmail());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole(userModel.getRole());
        user.setMobileNum(userModel.getMobileNum());
        user.setLastModified(System.currentTimeMillis());
        user.setDateCreated(System.currentTimeMillis());

        User userByMobileNum = userRepository.findUserByMobileNum(user.getMobileNum());
        if(userByMobileNum != null) {
            return newRestErrorResponse(409, "User with this mobile Number already exists");
        }

        User userByUserName = userRepository.findUserByUserName(user.getUserName());
        if (userByUserName != null) {
            return newRestErrorResponse(409, "User with this name already exists");
        }
        User savedUser = userRepository.save(user);
        publisher.publishEvent(new RegistrationControllerEvent(
                savedUser, tableNumber, applicationUrl(request)
        ));
        return newRestResponseData(String.format("Congrats, %s registered successfully !!!",
                Arrays.stream(userModel.getUserName().split("\\s")).toArray()[0]));
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
    }

    public void saveVerificationTokenForUser(String userId, long tableNumber, String token) {
        verificationTokenRepository.save(new VerificationToken(userId, tableNumber, token));
    }

    public VerificationEnum verifyVerificationToken(String token, Long tableNumber) {
        try {
            VerificationToken verificationToken = verificationTokenRepository.findVerificationTokenByTokenAndTableNumber(token, tableNumber);
            if (verificationToken == null) {
                return VerificationEnum.INVALID_TOKEN;
            }
            String userId = verificationToken.getUserId();
            userRepository.enableUserByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return VerificationEnum.EXPIRED_TOKEN;
        }
        return VerificationEnum.VALID_TOKEN;
    }
}
