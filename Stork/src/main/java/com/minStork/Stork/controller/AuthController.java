package com.minStork.Stork.controller;

import com.minStork.Stork.data.UserEntity;
import com.minStork.Stork.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty() ||
                    request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Fill all fields");
            }


            Optional<UserEntity> authenticatedUser = authService.login(
                    request.getIdentifier().trim(),
                    request.getPassword()
            );

            if (authenticatedUser.isPresent()) {
                UserEntity user = authenticatedUser.get();

                String jsonResponse = String.format(
                        "{\"token\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\"}",
                        user.getAuthToken(),
                        user.getFullName(),
                        user.getEmail()
                );
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