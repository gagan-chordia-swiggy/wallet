package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.UserRequest;
import com.example.wallet.exceptions.UserAlreadyExistsException;
import com.example.wallet.exceptions.UserNotFoundException;
import com.example.wallet.models.User;
import com.example.wallet.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ResponseEntity<ApiResponse> register(UserRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        ApiResponse response = ApiResponse.builder()
                .message("User registered successfully")
                .developerMessage("user registered")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .data(Map.of("user", user))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> login(UserRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        ApiResponse response = ApiResponse.builder()
                .message("User logged in")
                .developerMessage("logged in")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("user", user, "access-token", accessToken, "refresh-token", refreshToken))
                .build();


        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
