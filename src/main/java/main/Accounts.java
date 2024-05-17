package main;

public class Accounts implements IAccounts {
    private Database db;

    public Accounts(Database db){
        this.db = db;
    }

    public boolean accountExists(String username){
        return db.accountExists(username);
    }


    public String createAccount(String username, String password, String role, User currentUser) {
        if (!currentUser.hasPermission(Permissions.CAN_CREATE_ACCOUNT)){
            return "You don't have permission to create an account";
        }

        if (accountExists(username)) {
            return "Username already exists. Please choose a different username.";
        }

        db.createAccount(username, role, password);
        return "Account Created Successfully!";
    }

    public String deleteAccount(User currentUser, String username) {
        if (!accountExists(username)){
            return "No such account exists";
        }

        User user = db.getUser(username); // getting role and creating a main.User object
        if (user.isAdmin() && !user.getUsername().equals(username)) {
            return "Admin account cannot be deleted.";
        }
        else if (username.equals(currentUser.getUsername()) || currentUser.isAdmin()) {
            db.deleteUser(username);
            return "Account deleted successfully.";
        }
        else {
            return "You can only delete your own account or delete other accounts if you are an admin.";
        }
    }

    public User authenticateUser(String username, String password){
        return db.authenticateUser(username, password);
    }
}
