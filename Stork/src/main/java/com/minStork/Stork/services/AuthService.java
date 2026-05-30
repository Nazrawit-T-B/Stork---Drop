package com.minStork.Stork.services;

import com.minStork.Stork.data.UserEntity;
import com.minStork.Stork.data.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    // CHANGED: Now returns the UserEntity with a newly generated token if validation passes
    public Optional<UserEntity> login(String input, String password) {
        Optional<UserEntity> userOpt = repo.findByUsername(input);

        if (userOpt.isEmpty()) {
            userOpt = repo.findByEmail(input);
        }

        if (userOpt.isPresent() && encoder.matches(password, userOpt.get().getPasswordHash())) {
            UserEntity user = userOpt.get();
            
            // Generate a secure, unique token string from scratch
            String uniqueToken = "STORK-TOKEN-" + UUID.randomUUID().toString().toUpperCase();
            user.setAuthToken(uniqueToken);
            
            // Persist the token to the database record
            repo.save(user);
            
            return Optional.of(user);
        }

        return Optional.empty();
    }

    public boolean register(String fullName, String username, String email, String password) {
        if (repo.findByUsername(username).isPresent() || repo.findByEmail(email).isPresent()) {
            return false;
        }

        UserEntity u = new UserEntity();
        u.setFullName(fullName);
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(password));

        repo.save(u);
        return true;
    }
}
