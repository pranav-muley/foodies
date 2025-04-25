package com.feastora.food_ordering.controller;

import com.feastora.food_ordering.HttpResponse.BaseResponse;
import com.feastora.food_ordering.HttpResponse.GenericResponse;
import com.feastora.food_ordering.service.QrService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qr")
public class GenrateQRController extends BaseResponse {

    private final QrService qrService;

    public GenrateQRController(QrService qrService) {
        this.qrService = qrService;
    }

    @GetMapping("/generate-url")
    public ResponseEntity<GenericResponse<String>> createQrToken(@RequestParam String userId, @RequestParam int tableNumber, HttpServletRequest request) {
        GenericResponse<String> response = qrService.generateUrl(userId, tableNumber, request);
        if(response.getError() != null) {
            return badRequest(response);
        }
        return newResponseOk(response);
    }
}
