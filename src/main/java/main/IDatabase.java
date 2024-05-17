package main;

import java.util.ArrayList;

interface IDatabase {
    void createTables();
    void createAdmin();
    boolean accountExists(String username);
    void createAccount(String username, String role, String password);
    User authenticateUser(String username, String password);
    void createUser(String username, String password);
    User getUser(String username);
    void deleteUser(String username);
    void updateItemStock(int itemId, int quantity);
    ArrayList<Item> getItems();
    void removeItem(int itemId);
    void insertItem(Item item);
    Item retrieveItem(int id);
}