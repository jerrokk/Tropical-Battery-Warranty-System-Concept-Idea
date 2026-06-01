package tropical;

import java.time.LocalDate;
import java.util.List;


import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== Tropical Battery Warranty System ===");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Select an option: ");
            int choice = sc.nextInt();
            sc.nextLine(); //newline

            switch (choice) {
                case 1 -> registerMenu();
                case 2 -> loginMenu();
                case 3 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }


    private static void registerMenu() {
        System.out.println("\n--- Registration ---");
        System.out.println("1. Register Customer");
        System.out.println("2. Register Worker");
        System.out.print("Choose user type: ");
        int type = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter TRN: ");
        String trn = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        if (type == 1) { // Customer
            System.out.print("First Name: ");
            String fname = sc.nextLine();
            System.out.print("Last Name: ");
            String lname = sc.nextLine();
            System.out.print("Email: ");
            String email = sc.nextLine();
            System.out.print("Contact Number: ");
            String contact = sc.nextLine();

            Customer c = new Customer(trn, password, fname, lname, email, contact);
            FileHandler.saveUser(c);
            System.out.println("Customer registered successfully!");
        } 
        else if (type == 2) { // Worker
            System.out.print("Employee ID: ");
            String empID = sc.nextLine();
            Worker w = new Worker(trn, password, empID);
            FileHandler.saveUser(w);
            System.out.println("Worker registered successfully!");
        } 
        else {
            System.out.println("Invalid user type.");
        }
    }


    private static void loginMenu() {
        System.out.println("\n--- Login ---");
        System.out.print("Enter TRN: ");
        String trn = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        Users user = FileHandler.login(trn, password);
        if (user == null) {
            System.out.println("Invalid TRN or Password!");
            return;
        }

        System.out.println("\nWelcome, " + user.getRole() + "!");
        System.out.println(user);

        if (user instanceof Worker) {
            workerMenu((Worker) user);
        } else if (user instanceof Customer) {
            customerMenu((Customer) user);
        }
    }


    private static void workerMenu(Worker worker) {
        while (true) {
            System.out.println("\n--- Worker Menu ---");
            System.out.println("1. Add Warranty");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addWarranty();
                case 2 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }


    private static void customerMenu(Customer customer) {
        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View Warranty Status");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewWarranties(customer.getTrn());
                case 2 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid option, try again.");
            }
        }
    }


    private static void addWarranty() {
        System.out.println("\n--- Add Warranty ---");
        System.out.print("Enter Customer TRN: ");
        String trn = sc.nextLine();
        System.out.print("Enter Product Name: ");
        String product = sc.nextLine();
        System.out.print("Enter Serial Number: ");
        String serial = sc.nextLine();
        System.out.print("Enter Purchase Date (YYYY-MM-DD): ");
        LocalDate purchase = LocalDate.parse(sc.nextLine());
        System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
        LocalDate expiry = LocalDate.parse(sc.nextLine());

        Warranty w = new Warranty(trn, product, serial, purchase, expiry);
        FileHandler.saveWarranty(w);
        System.out.println("✅ Warranty saved successfully!");
    }


    private static void viewWarranties(String trn) {
        List<Warranty> warranties = FileHandler.getCustomerWarranties(trn);
        if (warranties.isEmpty()) {
            System.out.println("No warranties found for TRN: " + trn);
        } else {
            System.out.println("\n--- Warranty Records ---");
            for (Warranty w : warranties) {
                System.out.println(w);
                System.out.println("-----------------------------");
            }
        }
    }
}

