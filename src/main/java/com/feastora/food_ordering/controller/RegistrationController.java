package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.Errorhandling.ResponseError;
import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.enums.VerificationEnum;
import com.feastora.food_ordering.model.UserModel;
import com.feastora.food_ordering.service.UserService;
import io.jsonwebtoken.Jwts;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app")
public class RegistrationController extends BaseResponse {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ResponseEntity<GenericResponse<String>> login(@RequestParam String username, @RequestParam String password) {
        GenericResponse<String> response = new GenericResponse<>();
        try {
            response = userService.getLoginDetails(username, password);
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
            response.setData("User Verified Successfully!!!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
