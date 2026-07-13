package com.omiew.texail.bulletin_board_java;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(
            String firstName,
            String lastName,
            String email,
            String username,
            String password
    ) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("This username is already in use.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("This email is already in use.");
        }
        User user = new User(firstName, lastName, email, username, password);
        return userRepository.save(user);
    }

    public User login(
           String username,
           String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && password.equals(user.get().getHashedPassword())) {
            return user.get();
        } else {
            throw new RuntimeException("The username or password is incorrect.");
        }
    }

    public void ensureUserNotBlocked(User user) {
        if (user.isBlocked()) {
            throw new RuntimeException("Blocked users cannot perform this action.");
        }
    }
}
