package com.ttn.ecommerce.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
