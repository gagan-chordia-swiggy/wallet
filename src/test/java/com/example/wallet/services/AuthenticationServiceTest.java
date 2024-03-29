package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.UserRequest;
import com.example.wallet.enums.Location;
import com.example.wallet.enums.Role;
import com.example.wallet.exceptions.InvalidCredentialsException;
import com.example.wallet.exceptions.MissingCredentialsException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

    @Mock
    private WalletService walletService;

    @InjectMocks
    private AuthenticationService authService;

    @BeforeEach
    void setup() {
        openMocks(this);
    }

    @Test
    void test_userRegisteredSuccessfully() {
        UserRequest request = UserRequest.builder()
                .name("name")
                .username("uname")
                .password("password")
                .location(Location.INDIA)
                .role(Role.USER)
                .build();
        User user = mock(User.class);

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        ResponseEntity<ApiResponse> response = authService.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("user registered", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_registeringUserFromUnitedStatesShouldHaveUSDAsCurrency() {
        UserRequest request = UserRequest.builder()
                .name("name")
                .username("uname")
                .password("password")
                .location(Location.UNITED_STATES)
                .role(Role.USER)
                .build();
        User user = mock(User.class);

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        ResponseEntity<ApiResponse> response = authService.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("user registered", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_registeringUserFromBritainShouldHaveGBPAsCurrency() {
        UserRequest request = UserRequest.builder()
                .name("name")
                .username("uname")
                .password("password")
                .location(Location.BRITAIN)
                .role(Role.USER)
                .build();
        User user = mock(User.class);

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        ResponseEntity<ApiResponse> response = authService.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("user registered", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_existingUsernameCannotBeRegisteredAgain_throwsException() {
        UserRequest request = UserRequest.builder()
                .name("name")
                .username("uname")
                .password("password")
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> {
            ResponseEntity<ApiResponse> response = authService.register(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("same username", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_loginUserSuccessfully() {
        UserRequest request = UserRequest.builder()
                .name(null)
                .username("uname")
                .password("password")
                .role(null)
                .build();
        User user = User.builder()
                .name(null)
                .username(request.getUsername())
                .password(request.getPassword())
                .role(null)
                .build();
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        ResponseEntity<ApiResponse> response = authService.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("logged in", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
    }

    @Test
    void test_loginUserWithWrongCredentials_throwsException() {
        UserRequest request = UserRequest.builder()
                .username("uname")
                .password("password")
                .build();
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> {
            ResponseEntity<ApiResponse> response = authService.login(request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("invalid credentials", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_noCredentialsWhileLogin_throwsException() {
        UserRequest request = UserRequest.builder().build();

        assertThrows(MissingCredentialsException.class, () -> {
            ResponseEntity<ApiResponse> response = authService.login(request);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertEquals("missing credentials", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }

    @Test
    void test_blankCredentialsWhileLogin_throwsException() {
        UserRequest request = UserRequest.builder()
                .username("")
                .password("")
                .build();

        assertThrows(MissingCredentialsException.class, () -> {
            ResponseEntity<ApiResponse> response = authService.login(request);

            assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
            assertEquals("missing credentials", Objects.requireNonNull(response.getBody()).getDeveloperMessage());
        });
    }
}
