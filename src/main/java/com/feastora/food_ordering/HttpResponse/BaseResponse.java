package com.feastora.food_ordering.HttpResponse;

import com.feastora.food_ordering.Errorhandling.ResponseError;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor
public class BaseResponse {

    public static <T> ResponseEntity<T> ok() {
        return new ResponseEntity<T>(HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> badRequest(T object) {
        return new ResponseEntity<T>(object, HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<T> gateWayTimeOut(T object) {
        return new ResponseEntity<T>(object, HttpStatus.GATEWAY_TIMEOUT);
    }

    public static <T> ResponseEntity<T> newResponseOk(T object) {
        return new ResponseEntity<>(object, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> notFound(T object) {
        return new ResponseEntity<T>(object, HttpStatus.NOT_FOUND);
    }

    public static <T> ResponseEntity<T> internalServerError(T object) {
        return new ResponseEntity<>(object, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    public static <T> ResponseEntity<T> conflictError(T object) {
        return new ResponseEntity<>(object, HttpStatus.CONFLICT);
    }

    public static <T> ResponseEntity<T> unauthorized(T object) {
        return new ResponseEntity<T>(object, HttpStatus.UNAUTHORIZED);
    }

    public static <T> ResponseEntity<T> created(T object) {
        return new ResponseEntity<T>(object, HttpStatus.CREATED);
    }

    protected static <T> GenericResponse<T> newRestErrorResponse(T data, int code, String errorMessage) {

        return new GenericResponse<T>(data, new ResponseError(code, errorMessage));
    }

    public static <T> GenericResponse<T> newRestErrorResponse(int code, String errorMessage) {
        return new GenericResponse<>(new ResponseError(code, errorMessage));
    }

    public static <T> GenericResponse<T> newRestErrorResponse(int code, String title, String errorMessage) {
        return new GenericResponse<>(new ResponseError(code, title, errorMessage));
    }

    public static <T> GenericResponse<T> newRestResponseData(T data) {
        return new GenericResponse<>(data, null);
    }
}
