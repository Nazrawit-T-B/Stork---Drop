package com.minStork.Stork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minStork.Stork.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*") 
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Fill all fields");
            }

            boolean loginSuccessful = authService.login(
                    request.getIdentifier().trim(), 
                    request.getPassword()
            );

            if (loginSuccessful) {
                
                String fullName = "Miki Buzu"; 
                String email = request.getIdentifier().contains("@") ? request.getIdentifier() : "miki@storkdrop.com";
                
                String jsonResponse = String.format("{\"fullName\":\"%s\",\"email\":\"%s\"}", fullName, email);
                return ResponseEntity.ok(jsonResponse);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        try {
            if (request.getFullName() == null || request.getFullName().trim().isEmpty() ||
                request.getUsername() == null || request.getUsername().trim().isEmpty() ||
                request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
                
                return ResponseEntity.badRequest().body("Please fill out all registration fields.");
            }

            boolean isRegistered = authService.register(
                request.getFullName().trim(),
                request.getUsername().trim(),
                request.getEmail().trim(),
                request.getPassword()
            );
            if (isRegistered) {
                return ResponseEntity.ok("Account created successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Username or Email is already taken.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing registration.");
        }
    }
}
