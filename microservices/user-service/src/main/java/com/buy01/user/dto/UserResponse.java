package com.buy01.user.dto;

import com.buy01.user.model.Role;
import lombok.Data;

@Data
public class UserResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private String avatar;
}
