package com.feastora.food_ordering.service;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.Utility.JwtUtil;
import com.feastora.food_ordering.config.ServerConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class QrService extends BaseResponse {
    private final JwtUtil jwtUtil;
    private final ServerConfig serverConfig;
    public QrService(JwtUtil jwtUtil, ServerConfig serverConfig) {
        this.jwtUtil = jwtUtil;
        this.serverConfig = serverConfig;
    }
    public GenericResponse<String> generateUrl(String userId, int tableNumber, HttpServletRequest request) {
        String token = jwtUtil.generateQRToken(userId, tableNumber);
        if (token == null) {
            return newRestErrorResponse(400, "INVALID TOKEN CREATED");
        }
        String url = serverConfig.applicationUrl(request) + "/api/customer/session/start?" + "token="+token;
        return newRestResponseData(url);
    }
}
