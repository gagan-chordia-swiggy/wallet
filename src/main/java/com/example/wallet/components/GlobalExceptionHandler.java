package com.example.wallet.components;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = InvalidAmountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleInvalidAmountException() {
        ApiResponse response = ApiResponse.builder()
                .message("Non positive amount has been entered")
                .developerMessage("Invalid amount")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = OverWithdrawalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleOverWithdrawalException() {
        ApiResponse response = ApiResponse.builder()
                .message("No sufficient balance")
                .developerMessage("Over withdrawal")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleUserAlreadyExistsException() {
        ApiResponse response = ApiResponse.builder()
                .message("User with same username exists")
                .developerMessage("same username")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleUserNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message("User not found")
                .developerMessage("user not found")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse> handleInvalidPasswordException() {
        ApiResponse response = ApiResponse.builder()
                .message("Invalid credentials")
                .developerMessage("invalid credentials")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.badRequest().body(response);
    }
}
