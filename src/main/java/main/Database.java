package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Database implements IDatabase {
    Connection connection;
    public Database(String connectionString, String username, String password){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            this.connection = DriverManager.getConnection(connectionString, username, password);
            System.out.println("Connection Established");

            this.createTables();
            this.createAdmin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createAdmin(){
        String query = "SELECT * FROM ims.users WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "admin");
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Admin user already exists");
                return;
            }

            // Create the admin account with role "admin"
            System.out.println("Creating admin account");
            this.createAccount("admin", Roles.ADMINISTRATOR, "admin");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertItem(Item item) {
        String query = "INSERT INTO ims.items (name, description, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, item.getName());
            statement.setString(2, item.getDescription());
            statement.setString(3, item.getCategory());
            statement.setInt(4, item.getQuantity());
            statement.setDouble(5, item.getPrice());
            statement.executeUpdate();
            System.out.println("main.Item inserted successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Item retrieveItem(int id) {
        String query = "SELECT * FROM ims.items WHERE id = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int itemId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                return new Item(itemId, name, description, category, quantity, price);
            } else {
                System.out.println("main.Item not found with id: " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean accountExists(String username) {
        String query = "SELECT * FROM ims.users WHERE username = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();  // Return true if a record is found
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createAccount(String username, String role, String password) {
        String hashedPassword = password;
        String query = "INSERT INTO ims.users (username, role, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, role);
            statement.setString(3, hashedPassword);
            statement.executeUpdate();
            System.out.println("Account created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUser(String username, String password) {
        String query = "SELECT * FROM ims.users WHERE username = ? AND password = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                return new User(username, role);
            } else {
                System.out.println("Invalid username or password");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createUser(String username, String password){
        String query = "INSERT INTO ims.users (username, password) VALUES (?, ?)";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            System.out.println("main.User created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User getUser(String username){
        String query = "SELECT * FROM ims.users WHERE username = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                User user = new User(username, role);
                return user;
            } else {
                System.out.println("main.User does not exists");
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteUser(String username) {
        String query = "DELETE FROM ims.users WHERE username = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("User " + username + " deleted successfully");
            } else {
                System.out.println("main.User not found with username: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateItemStock(int itemId, int quantity) {
        String query = "UPDATE ims.items SET quantity = ? WHERE id = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setInt(1, quantity); // Update quantity
            statement.setInt(2, itemId); // Update based on item ID
            statement.executeUpdate();
            System.out.println("main.Item stock updated successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Item> getItems() {
        ArrayList<Item> items = new ArrayList<>();
        String query = "SELECT * FROM ims.items";  // Select all items
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int itemId = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                String category = resultSet.getString("category");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");
                Item item = new Item(itemId, name, description, category, quantity, price);
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void removeItem(int itemId) {
        String query = "DELETE FROM ims.items WHERE id = ?";
        try {
            PreparedStatement statement = this.connection.prepareStatement(query);
            statement.setInt(1, itemId); // Delete based on item ID
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("main.Item removed successfully");
            } else {
                System.out.println("main.Item not found with id: " + itemId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables(){
        String createItemsTable = "CREATE TABLE IF NOT EXISTS ims.items (\n" +
                "  id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "  name VARCHAR(255) NOT NULL,\n" +
                "  description TEXT,\n" +
                "  category TEXT,\n" +
                "  quantity INT NOT NULL,\n" +
                "  price DOUBLE NOT NULL\n" +
                ");";

        String createUsersTable = "CREATE TABLE IF NOT EXISTS ims.users (\n" +
                "  id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "  role VARCHAR(255) NOT NULL,\n" +
                "  username VARCHAR(255) NOT NULL UNIQUE,\n" +
                "  password VARCHAR(255) NOT NULL\n" +
                ");";

        try {
            PreparedStatement statement = connection.prepareStatement(createItemsTable);
            statement.executeUpdate();
            System.out.println("Items table created successfully");

            statement = connection.prepareStatement(createUsersTable);
            statement.executeUpdate();
            System.out.println("Users table created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
