package com.omiew.texail.bulletin_board_java;

import org.springframework.stereotype.Component;

@Component
public class ConsoleUI {
    private final UserService userService;

    public ConsoleUI(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        System.out.println("Привяо");
    }
}
