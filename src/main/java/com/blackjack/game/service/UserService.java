package com.blackjack.game.service;

import com.blackjack.game.model.User;
import com.blackjack.game.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user.
     */
    public User registerUser(String email, String password, String createdBy) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hashing password
        user.setCreatedBy(createdBy);

        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedAt(currentTimestamp);
        user.setBalance(100);


        return userRepository.save(user);
    }

    /**
     * Authenticate user based on email and raw (plaintext) password.
     */
    public Optional<User> authenticate(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    /**
     * Update user information.
     */
    public Optional<User> updateUser(String email, String password, String updatedBy) {
        return userRepository.findByEmail(email).map(user -> {
            user.setEmail(email);
            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }
            user.setUpdatedBy(updatedBy);
            user.setUpdatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
            return userRepository.save(user);
        });
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}