package com.blackjack.game.controller;

import com.blackjack.game.model.User;
import com.blackjack.game.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Sign up a new user.
     */
    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestParam String email, @RequestParam String password) {
        User newUser = userService.registerUser(email, password, "system");
        return ResponseEntity.ok(newUser);
    }

    /**
     * Login a user and authenticate credentials.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        Optional<User> user = userService.authenticate(email, password);
        if (user.isPresent()) {
            // TODO: Generate and return JWT Token (returning a placeholder for now)
            String token = "JWT-TOKEN";
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }

    /**
     * Update user details.
     */
    @PutMapping("/update/{email}")
    public ResponseEntity<User> updateUser(
            @RequestParam String email,
            @RequestParam(required = false) String password) {
        return userService.updateUser(email, password, "system")
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}