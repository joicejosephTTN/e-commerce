package com.ttn.ecommerce.advice;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
