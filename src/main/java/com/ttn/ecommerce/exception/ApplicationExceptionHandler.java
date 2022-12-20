package com.ttn.ecommerce.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.UnauthorizedUserException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.security.auth.login.AccountLockedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @Autowired
    MessageSource messageSource;

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

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CustomErrorFormat> handleBadRequestException(BadRequestException ex, WebRequest request) throws BadRequestException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CustomErrorFormat> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex, WebRequest request) throws MissingServletRequestParameterException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomErrorFormat> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, WebRequest request) throws MethodArgumentTypeMismatchException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), messageSource.getMessage("api.error.pageNotFound",null, Locale.ENGLISH), request.getDescription(false), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorFormat, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<CustomErrorFormat> handlePropertyReferenceException(PropertyReferenceException ex, WebRequest request) throws PropertyReferenceException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), messageSource.getMessage("api.error.pageNotFound",null, Locale.ENGLISH), request.getDescription(false), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorFormat, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CustomErrorFormat> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) throws UnauthorizedException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(errorFormat, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomErrorFormat> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) throws AccessDeniedException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.FORBIDDEN);
        return new ResponseEntity<>(errorFormat, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(PasswordDoNotMatchException.class)
    public ResponseEntity<CustomErrorFormat> handlePasswordDoNotMatch(PasswordDoNotMatchException ex, WebRequest request) throws  PasswordDoNotMatchException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorFormat> handleNotFoundException(NotFoundException ex, WebRequest request) throws  NotFoundException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(errorFormat, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomErrorFormat> handleUserAlreadyExists(HttpRequestMethodNotSupportedException ex, WebRequest request) throws  UserAlreadyExistsException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(errorFormat, HttpStatus.METHOD_NOT_ALLOWED);
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
    public ResponseEntity<CustomErrorFormat> handleAccountLocked(AccountLockedException ex, WebRequest request) throws AccountLockedException{
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

    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<CustomErrorFormat> handleAddressNotFound(AddressNotFoundException ex, WebRequest request) throws  AddressNotFoundException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<CustomErrorFormat> handleAddressNotFound(InvalidFileFormatException ex, WebRequest request) throws  InvalidFileFormatException{
        CustomErrorFormat errorFormat = new CustomErrorFormat(LocalDateTime.now(), ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(errorFormat, HttpStatus.BAD_REQUEST);
    }
}
