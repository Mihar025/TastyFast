package com.misha.tastyfast.exception;

public class UserDoNotExistException  extends RuntimeException{
    public UserDoNotExistException(String message) {
        super(message);
    }
}
