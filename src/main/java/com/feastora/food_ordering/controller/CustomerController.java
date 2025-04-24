package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.model.CustomerSession;
import com.feastora.food_ordering.repository.CustomerSessionRepository;
import com.feastora.food_ordering.service.SessionService;
import com.feastora.food_ordering.store.InMemorySessionStore;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/customer")
public class CustomerController extends BaseResponse {

    private final SessionService sessionService;

    public CustomerController(SessionService sessionService, InMemorySessionStore sessionStore) {
        this.sessionService = sessionService;
    }

    @PostMapping("/session/refresh")
    public ResponseEntity<GenericResponse<String>> refreshSession(@RequestHeader("Authorization") String bearerToken) {
        String oldToken = bearerToken.replace("Bearer ", "");
        GenericResponse<String> response = sessionService.refreshCustomerSessionToken(oldToken);
        if(response.getError() != null) {
            return badRequest(response);
        }
        return newResponseOk(response);
    }

    @PostMapping("/session/initiate")
    public ResponseEntity<?> startSession(@RequestParam String token, HttpServletRequest request) {
        try {
            String sessionToken = sessionService.initiateSession(token, request);
            return newResponseOk(newRestResponseData(sessionToken));
        } catch (Exception e) {
            return badRequest(newRestErrorResponse(401, "Invalid QR token"));
        }
    }

    @PostMapping("/order")
    public ResponseEntity<GenericResponse<String>> placeOrder(@RequestHeader("Authorization") String bearerToken, HttpServletRequest request) {
        String sessionToken = bearerToken.replace("Bearer ", "");
        boolean valid = sessionService.validateSession(sessionToken, request);

        if (!valid) return unauthorized(newRestErrorResponse(414, "Session expired or invalid"));

        return newResponseOk(newRestResponseData("Order placed"));
    }
}

