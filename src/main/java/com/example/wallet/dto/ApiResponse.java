package com.example.wallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    @Builder.Default
    private Long timestamp = System.currentTimeMillis();

    private String message;
    private String developerMessage;
    private HttpStatus status;
    private Integer statusCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<?, ?> data;
}
