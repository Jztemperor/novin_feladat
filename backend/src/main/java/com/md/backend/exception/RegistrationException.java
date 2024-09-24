package com.md.backend.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class RegistrationException extends RuntimeException{
    private Map<String, String> fieldErrors = new HashMap<>();

    public RegistrationException(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}