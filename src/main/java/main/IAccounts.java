package main;

public interface IAccounts {
    boolean accountExists(String username);
    String createAccount(String username, String password, String role, User currentUser);
    String deleteAccount(User currentUser, String username);
    User authenticateUser(String username, String password);
}