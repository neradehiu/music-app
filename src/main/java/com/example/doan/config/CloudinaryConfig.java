package com.example.doan.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dwkohtn7d",
                "api_key", "893592253891984",
                "api_secret", "7JVwTCvXNW-3uzdCxxNY8LsHyP0"
        ));
    }
}
