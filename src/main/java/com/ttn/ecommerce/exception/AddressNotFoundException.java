package com.ttn.ecommerce.exception;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String errorMessage)  {
        super(errorMessage);
    }
}
