package com.example.doan.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password; // Giữ lại password vì User vẫn cần
    private String email;
    private int age;
    private String role;
}
