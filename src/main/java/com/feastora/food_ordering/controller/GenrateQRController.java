package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qr")
public class GenrateQRController {

    private final JwtUtil jwtUtil;

    public GenrateQRController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/generate")
    public String createQrToken(String userId, long tableNumber) {
        String token = jwtUtil.generateQRToken(userId, tableNumber);
        if (token == null) {
            return "INVALID TOKEN CREATED";
        }
        return token;
    }

}
