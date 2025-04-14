package com.feastora.food_ordering.HttpResponse;

import com.feastora.food_ordering.Errorhandling.ResponseError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private T data;
    private ResponseError error;

    public GenericResponse(ResponseError error) {
        this.error = error;
    }

}
