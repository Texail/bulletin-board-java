package com.omiew.texail.bulletin_board_java;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        AdSearchCriteria criteria = new AdSearchCriteria();
        executeSearchAndPaginate(criteria, "All Ads");
    }

    private void executeSearchAndPaginate(AdSearchCriteria criteria, String menuTitle) {
        int currentPage = 0;
        int pageSize = 5;
        boolean keepBrowsing = true;

        // Сортировка по дате публикации (новые сверху)
        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "publicationDate"));

        while (keepBrowsing) {
            pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "publicationDate"));
            Page<Ad> adPage = adService.searchAds(criteria, pageable);
            List<Ad> ads = adPage.getContent();

            System.out.println("\n--- " + menuTitle + " (Page " + (currentPage + 1) + " of " + adPage.getTotalPages() + ") ---");

            if (ads.isEmpty()) {
                System.out.println("No ads found for current criteria.");
            } else {
                for (Ad ad : ads) {
                    System.out.println(ad);
                }
            }

            // Меню навигации
            System.out.println("\n[Page " + (currentPage + 1) + " / " + Math.max(1, adPage.getTotalPages()) + "]");
            System.out.println("N - Next page | P - Previous page | Q - Quit to menu");
            System.out.print("Select action: ");
            String navChoice = scanner.nextLine().trim().toUpperCase();

            switch (navChoice) {
                case "N" -> {
                    if (adPage.hasNext()) {
                        currentPage++;
                    } else {
                        System.out.println("No next page.");
                    }
                }
                case "P" -> {
                    if (adPage.hasPrevious()) {
                        currentPage--;
                    } else {
                        System.out.println("No previous page.");
                    }
                }
                case "Q" -> keepBrowsing = false;
                default -> System.out.println("Invalid input. Use N, P or Q.");
            }
        }
    }

    private void searchAds() {
        System.out.println("\n--- Search Ads ---");
        AdSearchCriteria criteria = new AdSearchCriteria();

        boolean continueSearch = true;
        while (continueSearch) {
            System.out.println("\nSelect filter (or 0 to start search):");
            System.out.println("1. Keyword (title/description)");
            System.out.println("2. Author ID");
            System.out.println("3. Publication date range");
            System.out.println("4. Sort by date");
            if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
                System.out.println("5. Status");
            }
            System.out.println("0. Start search");
            System.out.print("Select: ");

            int choice = readChoice();
            switch (choice) {
                case 0 -> continueSearch = false;
                case 1 -> {
                    System.out.print("Keyword (title/description): ");
                    String keyword = scanner.nextLine();
                    if (!keyword.trim().isEmpty()) criteria.setKeyword(keyword);
                }
                case 2 -> {
                    System.out.print("Author ID: ");
                    try {
                        Long authorId = Long.parseLong(scanner.nextLine());
                        criteria.setAuthorId(authorId);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format.");
                    }
                }
                case 3 -> {
                    System.out.println("Date range (format: yyyy-MM-dd HH:mm):");
                    System.out.print("From (or press Enter to skip): ");
                    String fromStr = scanner.nextLine();
                    if (!fromStr.trim().isEmpty()) {
                        try {
                            LocalDateTime dateFrom = LocalDateTime.parse(
                                    fromStr,
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            );
                            criteria.setDateFrom(dateFrom);
                        } catch (Exception e) {
                            System.out.println("Invalid date format. Skipping.");
                        }
                    }

                    System.out.print("To (or press Enter to skip): ");
                    String toStr = scanner.nextLine();
                    if (!toStr.trim().isEmpty()) {
                        try {
                            LocalDateTime dateTo = LocalDateTime.parse(
                                    toStr,
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            );
                            criteria.setDateTo(dateTo);
                        } catch (Exception e) {
                            System.out.println("Invalid date format. Skipping.");
                        }
                    }
                }
                case 4 -> {
                    System.out.println("Sort direction:");
                    System.out.println("1. From New to Old");
                    System.out.println("2. From Old to New");
                    System.out.print("Select: ");
                    int sortChoice = readChoice();
                    if (sortChoice == 1) {
                        criteria.setSortDirection("NEW_TO_OLD");
                    } else if (sortChoice == 2) {
                        criteria.setSortDirection("OLD_TO_NEW");
                    }
                }
                case 5 -> {
                    if (currentUser != null && currentUser.getRole() == Role.ADMIN) {
                        System.out.print("Status (ACTIVE, INACTIVE, BANNED): ");
                        String statusStr = scanner.nextLine();
                        if (!statusStr.trim().isEmpty()) {
                            try {
                                criteria.setStatus(AdStatus.valueOf(statusStr.toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid status. Skipping.");
                            }
                        }
                    }
                }
                default -> System.out.println("Invalid input.");
            }
        }
        executeSearchAndPaginate(criteria, "Search Results");
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
            String price = scanner.nextLine();
//            try {
//                price = Float.parseFloat(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid price format. Please enter a number.");
//                return;
//            }

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
            String newPrice = currentAd.getPrice();

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
                            newPrice = input;
                        }
//                        if (!input.trim().isEmpty()) {
//                            try {
//                                newPrice = Float.parseFloat(input);
//                            } catch (NumberFormatException e) {
//                                System.out.println("Invalid price format. Keeping current price.");
//                            }
//                        }
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
