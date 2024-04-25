import java.util.ArrayList;

class User {
    private String role;
    private String username;
    private ArrayList<String> permissions;

    public User(String username, String role) {
        this.username = username;
        this.role = role;
        this.permissions = new ArrayList<>();

        // assigning permissions
        switch (role) {
            case Roles.ADMINISTRATOR:
                this.permissions.add(Permissions.CAN_UPDATE_STOCK);
                this.permissions.add(Permissions.CAN_VIEW_STOCK);
                this.permissions.add(Permissions.CAN_CREATE_ACCOUNT);
                break;
            case Roles.SUPPLIER:
                this.permissions.add(Permissions.CAN_VIEW_STOCK);
                break;
            case Roles.CASHIER:
                this.permissions.add(Permissions.CAN_UPDATE_STOCK);
                this.permissions.add(Permissions.CAN_VIEW_STOCK);
                break;
        }
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    public boolean isAdmin(){
        return this.role.equals(Roles.ADMINISTRATOR);
    }

    @Override
    public String toString() {
        return "User {" +
                "username='" + username + "\', " +
                "role='" + role + '\'' +
                '}';
    }
}
