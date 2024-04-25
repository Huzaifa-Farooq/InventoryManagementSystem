// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String dbURL = "jdbc:mysql://localhost:3306";
        String username = "root";
        String password = "root";

        Database db = new Database(dbURL, username, password);
        db.createTables();
        Accounts accounts = new Accounts(db);
        User currentUser = null;

        // Keep asking user to login till not successful
        while (currentUser == null) {
            System.out.println("LOGIN SCREEN");
            System.out.print("Username: ");
            String usernameInput = scanner.nextLine();
            System.out.print("Password: ");
            String passwordInput = scanner.nextLine();

            currentUser = accounts.authenticateUser(usernameInput, passwordInput);
        }

        // Display menu based on user role
        System.out.println("\nWelcome, " + currentUser.getUsername() + "!");
        while (currentUser != null) {
            printMainMenu();

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
//                    manageUsers(userAccounts, scanner);
                    break;
                case 2:
//                    manageItems(database, scanner);
                    break;
                case 3:
                    currentUser = null;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }


    }

    static void printMainMenu(){
        System.out.println("\nMenu:");
        System.out.println("1. Manage Users");
        System.out.println("  - Create User");
        System.out.println("  - Delete User");
        System.out.println("2. Manage Items");
        System.out.println("  - View Items");
        System.out.println("  - Add Item");
        System.out.println("  - Update Item Stock");
        System.out.println("  - Remove Item");
        System.out.println("3. Logout");
    }
}