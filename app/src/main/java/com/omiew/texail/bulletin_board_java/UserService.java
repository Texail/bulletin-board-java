package com.omiew.texail.bulletin_board_java;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User register(
            String firstName,
            String secondName,
            String email,
            String nickname,
            String password
    ) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new RuntimeException("This nickname is already in use.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw  new RuntimeException("This email is already in use.");
        }
        User user = new User(firstName, secondName, email, nickname, password);
        return userRepository.save(user);
    }
}
