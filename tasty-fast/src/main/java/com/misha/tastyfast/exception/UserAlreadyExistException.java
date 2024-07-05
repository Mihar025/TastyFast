package com.misha.tastyfast.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String message){
        super(message);
    }
}
