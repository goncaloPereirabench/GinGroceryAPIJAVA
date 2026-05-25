package com.goncalo.grocery_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponseDTO {

    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;

    public ErrorResponseDTO(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.fieldErrors = null;
    }
}