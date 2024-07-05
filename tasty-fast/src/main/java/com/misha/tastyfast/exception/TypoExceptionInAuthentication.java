package com.misha.tastyfast.exception;

public class TypoExceptionInAuthentication extends RuntimeException{

    public TypoExceptionInAuthentication(String message){
        super(message);
    }

}
