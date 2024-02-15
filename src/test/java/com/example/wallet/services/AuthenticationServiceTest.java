package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.UserRequest;
import com.example.wallet.enums.Role;
import com.example.wallet.exceptions.UserAlreadyExistsException;
import com.example.wallet.models.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_registerUserSuccessfully() {
        UserRequest request = UserRequest.builder()
                .username("username")
                .name("name")
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = authService.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully", Objects.requireNonNull(response.getBody()).getMessage());
    }

    @Test
    void test_RegisterUserWithExistingUsername_throwsException() {
        UserRequest request = UserRequest.builder()
                .username("username")
                .name("name")
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> {
            ResponseEntity<ApiResponse> response = authService.register(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("User with same username exists", Objects.requireNonNull(response.getBody()).getMessage());
        });
    }

    @Test
    void test_LoginUserSuccessfully() {
        User user = User.builder()
                .name("abc")
                .username("abc")
                .password("abc")
                .role(Role.USER)
                .build();
    }
}
