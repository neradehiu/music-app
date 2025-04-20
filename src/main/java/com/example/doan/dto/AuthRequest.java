package com.example.doan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // ✅ Constructor không tham số (cần cho Spring Boot)
@AllArgsConstructor // ✅ Constructor có tham số (tiện lợi khi khởi tạo đối tượng)
public class AuthRequest {
    private String username;
    private String password;
}
