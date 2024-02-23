package com.example.wallet.services;

import com.example.wallet.dto.ApiResponse;
import com.example.wallet.dto.UserRequest;
import com.example.wallet.dto.UserResponse;
import com.example.wallet.dto.WalletResponse;
import com.example.wallet.enums.Location;
import com.example.wallet.exceptions.InvalidCredentialsException;
import com.example.wallet.exceptions.InvalidLocationException;
import com.example.wallet.exceptions.MissingCredentialsException;
import com.example.wallet.exceptions.UserAlreadyExistsException;
import com.example.wallet.models.User;
import com.example.wallet.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final WalletService walletService;

    public ResponseEntity<ApiResponse> register(UserRequest request) {
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }

        if (!Arrays.asList(Location.values()).contains(request.getLocation())) {
            throw new InvalidLocationException();
        }

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .location(request.getLocation())
                .role(request.getRole())
                .build();
        walletService.create(user);
        userRepository.save(user);

        ApiResponse response = ApiResponse.builder()
                .message("User registered successfully")
                .developerMessage("user registered")
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.CREATED.value())
                .data(Map.of("user", new UserResponse(user)))
                .build();

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    public ResponseEntity<ApiResponse> login(UserRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new MissingCredentialsException();
        }

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        ApiResponse response = ApiResponse.builder()
                .message("User logged in")
                .developerMessage("logged in")
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(Map.of("user", new UserResponse(user), "access-token", accessToken, "refresh-token", refreshToken))
                .build();


        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
