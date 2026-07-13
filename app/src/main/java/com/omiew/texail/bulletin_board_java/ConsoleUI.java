package com.omiew.texail.bulletin_board_java;

import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class ConsoleUI {
    private final UserService userService;
    private User currentUser;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleUI(UserService userService) {
        this.userService = userService;
    }

    public void start() {
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                if (currentUser.getRole() == Role.ADMIN) {
                    showAdminMenu();
                } else {
                    showUserMenu();
                }
            }
        }
    }

    private void showAuthMenu() {
        System.out.println("\n--- Authorization menu  ---");
        System.out.println("1. Log-in");
        System.out.println("2. Register");
        System.out.println("0. Exit");
        System.out.print("Select an action: ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 0 -> System.exit(0);
            case 1 -> LoginUser();
            case 2 -> RegisterUser();
            default -> System.out.println("Invalid input.");
        }
    }
    private void LoginUser() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            currentUser = userService.login(username, password);
            System.out.println("You are logged in");
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }
    }
    private void RegisterUser() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();
            System.out.print("First name: ");
            String firstName = scanner.nextLine();
            System.out.print("Last name: ");
            String lastName = scanner.nextLine();

            currentUser = userService.register(firstName, lastName, email, username, password);
            System.out.println("Registration successful. You are automatically logged in.");
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
        }
    }
    private void showAdminMenu() {
        System.out.println("\n--- Admin menu " + currentUser.getUsername() + "---");
        System.out.println("1. View all ads");
        System.out.println("2. Search ads");
        System.out.println("3. My ads");
        System.out.println("4. Create ad");
        System.out.println("5. Block user");
        System.out.println("6. Block ad");
        System.out.println("0. Logout");

        System.out.print("Select an action: ");
        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1 -> viewAllAds();
            case 2 -> searchAds();
            case 3 -> viewMyAds();
            case 4 -> createAd();
            case 5 -> blockUser();
            case 6 -> blockAd();
            case 0 -> currentUser = null;
            default -> System.out.println("Invalid input");
        }
    }

    private void showUserMenu() {
        System.out.println("\n--- User menu  ---");
        if (currentUser.getStatus()) {
            System.out.println("Your account has been blocked!");
        }
        System.out.println("1. View all ads");
        System.out.println("2. Search ads");
        System.out.println("3. My ads");
        if (!currentUser.getStatus()) {
            System.out.println("4. Create ad");
        }
        System.out.println("0. Logout");

        System.out.print("Select an action: ");
        int choice = Integer.parseInt(scanner.nextLine());
        switch (choice) {
            case 1 -> viewAllAds();
            case 2 -> searchAds();
            case 3 -> viewMyAds();
            case 4 -> {
                if (!currentUser.getStatus()) createAd();
            }
            case 0 -> currentUser = null;
            default -> System.out.println("Invalid input");
        }
    }

    private void viewAllAds() {

    }
    private void searchAds() {

    }
    private void viewMyAds() {

    }
    private void createAd() {

    }
    private void blockUser() {

    }
    private void blockAd() {

    }
}
