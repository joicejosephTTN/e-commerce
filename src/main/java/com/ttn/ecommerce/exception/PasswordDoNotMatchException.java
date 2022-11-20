package com.ttn.ecommerce.exception;


public class PasswordDoNotMatchException extends RuntimeException{
    public PasswordDoNotMatchException(String errorMessage) {
        super(errorMessage);
    }
}
