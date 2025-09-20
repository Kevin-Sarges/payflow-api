package com.kevinsarges.payflow_api.utils;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
