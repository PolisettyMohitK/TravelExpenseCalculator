package com.travel.expense.service;

import com.travel.expense.dto.AuthResponse;
import com.travel.expense.dto.LoginRequest;
import com.travel.expense.dto.RegisterRequest;
import com.travel.expense.exception.ApiException;
import com.travel.expense.model.User;
import com.travel.expense.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        User user = new User(request.name().trim(), email, passwordEncoder.encode(request.password()));
        return issueToken(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = repository.findByEmail(normalizeEmail(request.email()))
                .filter(found -> passwordEncoder.matches(request.password(), found.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return issueToken(user);
    }

    public void logout(User user) {
        user.setToken(null);
        repository.save(user);
    }

    public User authenticate(String token) {
        return repository.findByToken(token)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Please log in to continue"));
    }

    // Reuses an existing token so the user can stay logged in on several devices
    private AuthResponse issueToken(User user) {
        if (user.getToken() == null) {
            user.setToken(UUID.randomUUID().toString());
        }
        repository.save(user);
        return new AuthResponse(user.getToken(), user.getName(), user.getEmail());
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
