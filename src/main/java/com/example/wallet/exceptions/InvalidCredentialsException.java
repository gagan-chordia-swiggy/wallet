package com.example.wallet.exceptions;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

public class InvalidCredentialsException extends AuthenticationCredentialsNotFoundException {
    public InvalidCredentialsException() {
        super("Wrong credentials");
    }
}
