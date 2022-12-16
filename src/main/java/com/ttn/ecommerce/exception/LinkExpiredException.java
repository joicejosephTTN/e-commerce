package com.ttn.ecommerce.exception;

public class LinkExpiredException extends RuntimeException{
    public LinkExpiredException(String errorMessage) {
        super(errorMessage);
    }
}
