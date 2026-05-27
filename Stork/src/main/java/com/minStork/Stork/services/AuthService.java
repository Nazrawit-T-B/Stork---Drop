package com.minStork.Stork.services;

import com.minStork.Stork.data.UserEntity;
import com.minStork.Stork.data.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import encoder
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(); // Initialize encoder

    public AuthService(UserRepository repo) {
        this.repo = repo;
    }


    public boolean login(String input, String password) {
        Optional<UserEntity> user = repo.findByUsername(input);

        if (user.isEmpty()) {
            user = repo.findByEmail(input);
        }

        return user.map(u -> encoder.matches(password, u.getPasswordHash()))
                   .orElse(false);
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
