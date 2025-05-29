package com.example.doan.controller;

import com.example.doan.dto.AuthRequest;
import com.example.doan.dto.AuthResponse;
import com.example.doan.dto.RegisterRequest;
import com.example.doan.model.User;
import com.example.doan.repository.UserRepository;
import com.example.doan.security.JwtUtil;
import com.example.doan.security.TokenBlacklistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
        "http://10.0.2.2:8000",    // Flutter chạy trên Android Emulator
        "https://music-app-b1ef.onrender.com", // Add Render URL here
        "https://music-app-1-f1ec.onrender.com",
        "https://aisearchbyvoice.onrender.com"
}, allowCredentials = "true")
public class AuthController {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // ✅ Sử dụng PasswordEncoder
    private final TokenBlacklistService tokenBlacklistService;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    // ✅ Đăng ký tài khoản
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists!");
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole() != null ? request.getRole() : "ROLE_USER"
        );

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // ✅ Đăng nhập
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(null, "Invalid username or password"));
        }

        User user = userOpt.get();
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(new AuthResponse(token, "Login successful"));
    }

    // ✅ Đăng xuất
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        tokenBlacklistService.blacklistToken(token);
        return ResponseEntity.ok("Logged out successfully!");
    }

    // ✅ API kiểm tra token hợp lệ và lấy role
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        token = token.replace("Bearer ", ""); // ✅ Xóa tiền tố "Bearer "
        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);

        if (username == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }

        return ResponseEntity.ok("Valid token. Role: " + role);
    }
}
