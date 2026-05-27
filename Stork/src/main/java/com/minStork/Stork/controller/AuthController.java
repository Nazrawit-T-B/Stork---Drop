package com.minStork.Stork.controller;

import com.minStork.Stork.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*") 
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService; 

   @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        try {
            // 1. Basic structural checks
            if (request.getIdentifier() == null || request.getIdentifier().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username or Email is required!");
            }
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Password is required!");
            }

            // 2. Delegate to AuthService which uses encoder.matches()
            boolean loginSuccessful = authService.login(
                    request.getIdentifier().trim(), 
                    request.getPassword()
            );

            if (loginSuccessful) {
                return ResponseEntity.ok("Login successful!");
            } else {
                // Returns unauthorized status if password match fails or user isn't found
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid username/email or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ERROR: " + e.getMessage());
        }
    }
}
