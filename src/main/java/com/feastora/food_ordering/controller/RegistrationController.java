package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.Errorhandling.ResponseError;
import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.enums.VerificationEnum;
import com.feastora.food_ordering.model.UserModel;
import com.feastora.food_ordering.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class RegistrationController extends BaseResponse {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public RegistrationController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<String>> login(@RequestBody UserModel userModel) {
        GenericResponse<String> response = new GenericResponse<>();
        try {
            response = userService.getLoginDetails(userModel);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return newResponseOk(response);
    }

    @PostMapping("/register")
    public ResponseEntity<GenericResponse<String>> registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        GenericResponse<String> response = userService.registerUser(userModel, request);
        if(response.getError() != null) {
            return conflictError(response);
        }
        return newResponseOk(response);
    }

    @GetMapping("/verifyRegistration")
    public ResponseEntity<GenericResponse<String>> verifyToken(@RequestParam String token) {
        GenericResponse<String> response = new GenericResponse<>();
        VerificationEnum status = userService.verifyVerificationToken(token);
        if(status == VerificationEnum.EXPIRED_TOKEN) {
            response.setError(new ResponseError("Token is Expired", "Please Register again !!!", 409));
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } else if (status == VerificationEnum.INVALID_TOKEN) {
            response.setError(new ResponseError("Token is invalid", "Please try again!!!", 409));
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } else {
            response.setData(String.format("Great! %s has been verified Successfully!!!", jwtUtil.extractValueByKey(token, "username")));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
