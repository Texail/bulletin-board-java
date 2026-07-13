package com.omiew.texail.bulletin_board_java;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class ConsoleUI {
    private final UserService userService;
    private final AdService adService;
    private User currentUser;
    private Scanner scanner = new Scanner(System.in);

    public ConsoleUI(
            UserService userService,
            AdService adService
    ) {
        this.userService = userService;
        this.adService = adService;
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
        int choice = readChoice();

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
        int choice = readChoice();
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
        if (currentUser.isBlocked()) {
            System.out.println("Your account has been blocked!");
        }
        System.out.println("1. View all ads");
        System.out.println("2. Search ads");
        System.out.println("3. My ads");
        if (!currentUser.isBlocked()) {
            System.out.println("4. Create ad");
        }
        System.out.println("0. Logout");

        System.out.print("Select an action: ");
        int choice = readChoice();
        switch (choice) {
            case 1 -> viewAllAds();
            case 2 -> searchAds();
            case 3 -> viewMyAds();
            case 4 -> {
                if (!currentUser.isBlocked()) createAd();
            }
            case 0 -> currentUser = null;
            default -> System.out.println("Invalid input");
        }
    }

    private void viewAllAds() {
        try {
            List<Ad> ads = adService.getVisibleAds();
            printAds(ads);
        } catch (Exception e) {
            System.out.println("View error: " + e.getMessage());
        }
    }

    private void printAds(List<Ad> ads) {
        if (ads.isEmpty()) {
            System.out.println("No ads found");
            return;
        }
        System.out.println("\n--- List of ads ---");
        for (Ad ad : ads) {
            System.out.println(ad);
        }
    }
    private void searchAds() {

    }
    private void viewMyAds() {
        try {
            List<Ad> ads = adService.getUserAds(currentUser);
            printAds(ads);
            if (!ads.isEmpty() && !currentUser.isBlocked()) {
                System.out.print("Enter the ad ID to manage (or \"Enter\" to cancel): ");
                String idStr = scanner.nextLine();
                if (!idStr.isEmpty()) {
                    manageAd(Long.parseLong(idStr));
                }
            }
        } catch (Exception e) {
            System.out.println("View error: " + e.getMessage());
        }

    }
    private void createAd() {
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            System.out.print("Price: ");
            float price;
            try {
                price = Float.parseFloat(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid price format. Please enter a number.");
                return;
            }

            adService.createAd(title, description, price, currentUser);
        } catch (Exception e) {
            System.out.println("Creation error: " + e.getMessage());
        }
    }
    private void manageAd(Long adId) {
        System.out.println("1. Edit");
        System.out.println("2. Deactivate / Activate");
        System.out.print("Select an action: ");
        int choice = readChoice();

        try {
            switch (choice){
                case 1 -> editAd(adId);
                case 2 -> {
                    Ad updated = adService.toggleAdStatus(adId, currentUser);
                    System.out.println("Status changed to: " + updated.getStatus());
                }
                default -> System.out.println("Invalid input.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void editAd(Long adId) {
        try {
            Ad currentAd = adService.getAdById(adId);

            userService.ensureUserNotBlocked(currentUser);
            adService.ensureAdNotBlocked(currentAd);
            adService.checkAdOwnership(currentAd, currentUser);

            System.out.println("\n--- Edit Ad ---");

            String newTitle = currentAd.getTitle();
            String newDesc = currentAd.getDescription();
            Float newPrice = currentAd.getPrice();

            boolean keepEditing = true;
            while (keepEditing) {
                System.out.println("\nWhat do you want to update?");
                System.out.println("\nCurrent values:");
                System.out.println("1. Title: " + newTitle);
                System.out.println("2. Description: " + newDesc);
                System.out.println("3. Price: " + newPrice);
                System.out.println("0. Save changes and exit");
                System.out.print("Select an action: ");

                int choice = readChoice();

                switch (choice) {
                    case 0 -> keepEditing = false;
                    case 1 -> {
                        System.out.print("Enter new title (or press Enter to keep current): ");
                        String input = scanner.nextLine();
                        if (!input.trim().isEmpty()) {
                            newTitle = input;
                        }
                    }
                    case 2 -> {
                        System.out.print("Enter new description (or press Enter to keep current): ");
                        String input = scanner.nextLine();
                        if (!input.trim().isEmpty()) {
                            newDesc = input;
                        }
                    }
                    case 3 -> {
                        System.out.print("Enter new price (or press Enter to keep current): ");
                        String input = scanner.nextLine();
                        if (!input.trim().isEmpty()) {
                            try {
                                newPrice = Float.parseFloat(input);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid price format. Keeping current price.");
                            }
                        }
                    }
                    default -> System.out.println("Invalid input.");
                }
            }

            Ad updatedAd = adService.updateAd(adId, newTitle, newDesc, newPrice, currentUser);
            System.out.println("Ad updated successfully!");

        } catch (Exception e) {
            System.out.println("Edit error: " + e.getMessage());
        }
    }

    private void blockUser() {

    }
    private void blockAd() {

    }

    private int readChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
