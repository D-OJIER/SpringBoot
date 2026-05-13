package com.management.water.security.dto;

import lombok.Data;

@Data
public class LoginRequest {
    
    private String username;
    private String password;
}
