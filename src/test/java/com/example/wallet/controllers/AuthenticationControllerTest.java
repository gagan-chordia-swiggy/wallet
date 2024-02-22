package com.example.wallet.controllers;

import com.example.wallet.dto.UserRequest;
import com.example.wallet.enums.Role;
import com.example.wallet.exceptions.InvalidCredentialsException;
import com.example.wallet.exceptions.UserAlreadyExistsException;
import com.example.wallet.services.AuthenticationService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        reset(authenticationService);
    }

    @Test
    void test_userRegisteredSuccessfully() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("name")
                .username("uname")
                .password("password")
                .role(Role.USER)
                .build();
        String request = objectMapper.writeValueAsString(userRequest);

        when(authenticationService.register(userRequest)).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/api/v1/users")
                .content(request)
                .contentType("application/json")
        ).andExpect(status().isCreated());
        verify(authenticationService, times(1)).register(userRequest);
    }

    @Test
    void test_existingUsernameCannotBeRegisteredAgain_throwsException() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .name("name")
                .username("uname")
                .password("password")
                .role(Role.USER)
                .build();
        String request = objectMapper.writeValueAsString(userRequest);

        when(authenticationService.register(userRequest)).thenThrow(new UserAlreadyExistsException());

        mockMvc.perform(post("/api/v1/users")
                .content(request)
                .contentType("application/json")
        ).andExpect(status().isBadRequest());
        verify(authenticationService, times(1)).register(userRequest);
    }

    @Test
    void test_loginUserSuccessfully() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("uname")
                .password("password")
                .build();
        String request = objectMapper.writeValueAsString(userRequest);

        when(authenticationService.login(userRequest)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(post("/api/v1/users/auth")
                .contentType("application/json")
                .content(request)
        ).andExpect(status().isOk());
        verify(authenticationService, times(1)).login(userRequest);
    }

    @Test
    void test_loginUserWithWrongCredentials_throwsException() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("uname")
                .password("password")
                .build();
        String request = objectMapper.writeValueAsString(userRequest);

        when(authenticationService.login(userRequest)).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/users/auth")
                .content(request)
                .contentType("application/json")
        ).andExpect(status().isBadRequest());
        verify(authenticationService, times(1)).login(userRequest);
    }
}
