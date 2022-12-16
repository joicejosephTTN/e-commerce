package com.ttn.ecommerce.exception;

import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {
    public UnauthorizedException(String errorMessage){
        super(errorMessage);
    }
}

