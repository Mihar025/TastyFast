package com.misha.tastyfast.exception;

public class AccountDissabledException extends RuntimeException {
    public AccountDissabledException(String accountIsDisabled) {
        super(accountIsDisabled);
    }
}
