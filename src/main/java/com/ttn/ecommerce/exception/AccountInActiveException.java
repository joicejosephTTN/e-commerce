package com.ttn.ecommerce.exception;

public class AccountInActiveException extends RuntimeException {
    public AccountInActiveException(String errorMessage)  {
        super(errorMessage);
    }
}
