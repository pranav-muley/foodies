package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/QR")
public class GenrateQRController {

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("")
    public String createQrToken(String userId, long tableNumber) {
        String token = jwtUtil.generateQRToken(userId, tableNumber);

        return token;
    }

}
