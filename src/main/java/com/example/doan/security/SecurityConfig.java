package com.example.doan.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS configuration
                .csrf(csrf -> csrf.disable())  // Disable CSRF as you are using JWT
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // Allow public access to auth endpoints
                        .requestMatchers("/api/songs/favorites", "/api/songs/{id}/favorite").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")  // Allow access for both USER and ADMIN
                        .requestMatchers(HttpMethod.GET,  "/api/songs", "/api/songs/{id}", "/api/songs/{id}/play", "/api/songs/search", "/api/songs/genre/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST,  "/api/songs/upload").hasAuthority("ROLE_ADMIN")  // Only ADMIN can upload songs
                        .requestMatchers(HttpMethod.PUT, "/api/songs/{id}").hasAuthority("ROLE_ADMIN")  // Only ADMIN can update songs
                        .requestMatchers(HttpMethod.DELETE, "/api/songs/{id}").hasAuthority("ROLE_ADMIN")  // Only ADMIN can delete songs
                        .anyRequest().authenticated()  // All other requests need to be authenticated
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Stateless session (JWT)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  // JWT filter

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // BCrypt password encoder
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();  // Authentication manager for authentication
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://music-app.onrender.com"));  // Allow frontend URL from Render
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // Allow all common HTTP methods
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));  // Allow headers needed for JWT and content type
        configuration.setExposedHeaders(List.of("Authorization"));  // Expose Authorization header for frontend to get JWT
        configuration.setAllowCredentials(true);  // Allow credentials (cookies, etc.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // Apply CORS configuration to all endpoints
        return source;
    }
}
