package com.userMS.UserMicroService.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorResponse handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        return new ErrorResponse("Conflict", exception.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException exception) {
        return new ErrorResponse("Unauthorized", exception.getMessage());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleInvalidCredentialsException(InvalidCredentialsException exception) {
        return new ErrorResponse("Unauthorized", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGeneralException(Exception exception) {
        return new ErrorResponse("Internal Server Error", "An internal exception occurred");
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleUserDoesNotExistExceptionException(UserDoesNotExistException exception) {
        return new ErrorResponse("Not found", exception.getMessage());
    }
}
