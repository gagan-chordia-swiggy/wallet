package com.example.wallet.dto;

import com.example.wallet.enums.Location;
import com.example.wallet.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponse {
    private String name;
    private String username;
    private Location location;

    public UserResponse(User user) {
        this.name = user.getName();
        this.username = user.getUsername();
        this.location = user.getLocation();
    }
}
