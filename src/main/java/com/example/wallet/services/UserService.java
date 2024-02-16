package com.example.wallet.services;

import com.example.wallet.models.User;
import com.example.wallet.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void deleteUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        userRepository.delete(user);
    }
}
