package com.misha.tastyfast.exception;

public class EmailorPasswordAlreadyExistException extends RuntimeException {
    public EmailorPasswordAlreadyExistException(String message){
        super(message);
    }
}
