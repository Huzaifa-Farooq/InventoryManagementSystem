public class Accounts {
    private Database db;

    public Accounts(Database db){
        this.db = db;
    }

    boolean accountExists(String username){
        return db.accountExists(username);
    }


    public User createAccount(String username, String password, String role, User currentUser) {
        if (!currentUser.isAdmin()) {
            System.out.println("Only admins can create accounts.");
            return null;
        }

        if (accountExists(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return null;
        }

        db.createAccount(username, role, password);
        return new User(role, username);
    }

    public void deleteAccount(User currentUser, String username) {
        User user = db.getUser(username); // getting role and creating a User object
        if (user.isAdmin()) {
            System.out.println("Admin account cannot be deleted.");
        }
        else if (username.equals(currentUser.getUsername()) || currentUser.isAdmin()) {
            db.deleteUser(username);
            System.out.println("Account deleted successfully.");
        }
        else {
            System.out.println("You can only delete your own account or delete other accounts if you are an admin.");
        }
    }

    public User authenticateUser(String username, String password){
        return db.authenticateUser(username, password);
    }
}
