package com.ttn.ecommerce.advice;


public class PasswordDoNotMatchException extends RuntimeException{
    public PasswordDoNotMatchException(String errorMessage) {
        super(errorMessage);
    }
}
