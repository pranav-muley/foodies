package com.feastora.food_ordering.Errorhandling;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseError {
    private  String errorMessage;
    private String errorTitleMsg;
    private  int errorCode;

    public ResponseError(int code, String title, String errorMessage) {
        this.errorCode = code;
        this.errorTitleMsg = title;
        this.errorMessage = errorMessage;
    }

    public ResponseError(int code, String errorMessage) {
        this.errorCode = code;
        this.errorMessage = errorMessage;
    }
}
