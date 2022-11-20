package com.ttn.ecommerce.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
