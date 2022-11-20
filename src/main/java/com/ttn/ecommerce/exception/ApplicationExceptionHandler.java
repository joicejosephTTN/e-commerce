package com.ttn.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.security.auth.login.AccountLockedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<CustomErrorFormat> handleAllException(Exception ex, WebRequest request) throws Exception{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorFormat, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String,String> handleInvalidArguments(MethodArgumentNotValidException ex){
        Map<String,String> errorMap = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors() // returns a list of errors
                .forEach(fieldError -> {
                    // populating map with, field with error as key, and relevant message as value
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());

                });
        return errorMap;
    }


    @ExceptionHandler(PasswordDoNotMatchException.class)
    public ResponseEntity<CustomErrorFormat> handlePasswordDoNotMatch(PasswordDoNotMatchException ex, WebRequest request) throws  PasswordDoNotMatchException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<CustomErrorFormat> handleUserAlreadyExists(UserAlreadyExistsException ex, WebRequest request) throws  UserAlreadyExistsException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomErrorFormat> handleBadCredentials(BadCredentialsException ex, WebRequest request) throws  BadCredentialsException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<CustomErrorFormat> handleAccountLocked(AccountLockedException ex, WebRequest request){
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(errorFormat, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomErrorFormat> handleUserNotFound(UsernameNotFoundException ex, WebRequest request){
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(errorFormat, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public final ResponseEntity<CustomErrorFormat> handleInvalidToken(InvalidTokenException ex, WebRequest request){
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorFormat, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(LinkExpiredException.class)
    public final ResponseEntity<CustomErrorFormat> handleLinkExpired(LinkExpiredException ex, WebRequest request){
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(errorFormat, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}