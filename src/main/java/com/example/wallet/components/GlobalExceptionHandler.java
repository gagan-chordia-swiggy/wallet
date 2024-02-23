package com.example.wallet.components;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.exceptions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = InvalidAmountException.class)
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
    public ResponseEntity<ApiResponse> handleInvalidPasswordException() {
        ApiResponse response = ApiResponse.builder()
                .message("Invalid credentials")
                .developerMessage("invalid credentials")
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(value = TransactionNotFoundException.class)
    public ResponseEntity<ApiResponse> handleTransactionNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message("Transaction not found")
                .developerMessage("transaction not found")
                .statusCode(HttpStatus.NOT_FOUND.value())
                .status(HttpStatus.NOT_FOUND)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(value = MissingCredentialsException.class)
    public ResponseEntity<ApiResponse> handleMissingCredentialsException() {
        ApiResponse response = ApiResponse.builder()
                .message("Missing credentials")
                .developerMessage("missing credentials")
                .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .build();

        return ResponseEntity.unprocessableEntity().body(response);
    }

    @ExceptionHandler(value = UnauthorizedWalletAccessException.class)
    public ResponseEntity<ApiResponse> handleUnauthorizedWalletAccessException() {
        ApiResponse response = ApiResponse.builder()
                .message("Access to other wallets is not permitted")
                .developerMessage("unauthorized wallet access")
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = TransactionForSameUserException.class)
    public ResponseEntity<ApiResponse> handleTransactionForSameUserException() {
        ApiResponse response = ApiResponse.builder()
                .message("Cannot transact with self")
                .developerMessage("transaction with self")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = WalletNotFoundException.class)
    public ResponseEntity<ApiResponse> handleWalletNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message("Cannot find the wallet")
                .developerMessage("wallet not found")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = InvalidLocationException.class)
    public ResponseEntity<ApiResponse> handleInvalidLocationException() {
        ApiResponse response = ApiResponse.builder()
                .message("Our service is not supported in the given location")
                .developerMessage("unsupported location")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = CurrencyNotFoundException.class)
    public ResponseEntity<ApiResponse> handleCurrencyNotFoundException() {
        ApiResponse response = ApiResponse.builder()
                .message("The shared currency is not supported at the moment")
                .developerMessage("unsupported currency")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @ExceptionHandler(value = CurrencyAlreadyExistsException.class)
    public ResponseEntity<ApiResponse> handleCurrencyAlreadyExistsException() {
        ApiResponse response = ApiResponse.builder()
                .message("Currency has already been added")
                .developerMessage("duplicate currency")
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
