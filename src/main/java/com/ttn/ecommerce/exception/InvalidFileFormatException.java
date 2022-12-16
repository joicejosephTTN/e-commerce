package com.ttn.ecommerce.exception;

public class InvalidFileFormatException extends RuntimeException{
    public InvalidFileFormatException(String errorMessage)  {
        super(errorMessage);
    }
}
