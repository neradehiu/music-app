package com.example.doan.service;

import com.example.doan.dto.AuthRequest;
import com.example.doan.dto.AuthResponse;
import com.example.doan.dto.RegisterRequest;
import com.example.doan.model.User;
import com.example.doan.repository.UserRepository;
import com.example.doan.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // Xử lý đăng ký tài khoản
    public String register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            return "Username already exists!";
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "User registered successfully!";
    }

    // Xử lý đăng nhập
    public AuthResponse login(AuthRequest request) {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isEmpty() || !passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            return new AuthResponse(null, "Invalid username or password!");
        }

        String token = jwtUtil.generateToken(user.get().getUsername(), user.get().getRole());
        return new AuthResponse(token, "Login successful!");
    }
}
