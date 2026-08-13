package com.example.library.service;

import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> login(String email, String rawPassword) {
        return userRepository.findByEmailIgnoreCase(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    public User register(User user) {
        String normalizedEmail = user.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalStateException("ایمیل واردشده قبلاً ثبت شده است.");
        }

        user.setId(null);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
