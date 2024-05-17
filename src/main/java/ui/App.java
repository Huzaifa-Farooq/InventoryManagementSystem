package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


import main.*;

public class App {
    private JFrame frame;
    private JPanel cards;
    private CardLayout cardLayout;

    private Accounts accounts;
    private Inventory inventory;
    private User user;
    private JTable table;

    private DefaultTableModel model;
    // To be able to remove this from frame
    private JPanel buttonsPanel;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public App() {
        Database db = new Database(
                "jdbc:mysql://localhost:3306", "root", "root");
        this.accounts = new Accounts(db);
        this.inventory = new Inventory(db);
        this.user = null;

        createGUI();
    }

    public void refreshInventoryTable() {
        setInventoryTable(inventory.getInventoryItems());
    }

    public void createGUI() {
        frame = new JFrame("Inventory Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JLabel("Inventory Management System"));

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JPanel loginPanel = createLoginPanel();
        JPanel createAccountPanel = createCreateAccountPanel();
        JPanel inventoryPanel = createInventoryPanel();
        JPanel addItemPanel = createAddItemPanel();
        JPanel deleteAccountPanel = createDeleteAccountPanel();
        JPanel performSalesPanel = createPerformSalesPanel();

        cards.add(loginPanel, "Login");
        cards.add(createAccountPanel, "CreateAccount");
        cards.add(inventoryPanel, "Inventory");
        cards.add(addItemPanel, "AddItem");
        cards.add(deleteAccountPanel, "DeleteAccount");
        cards.add(performSalesPanel, "PerformSales");

        frame.add(cards);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    private JPanel createMenuButtonsPanel() {
        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getUser() != null){
                    cardLayout.show(cards, "Inventory");
                } else {
                    cardLayout.show(cards, "Login");
                }
            }
        });
        buttonPanel.add(homeButton);

        // Create buttons and add them to the panel
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!getUser().isAdmin()) {
                    JOptionPane.showMessageDialog(frame, "Only admin can create account");
                } else {
                    cardLayout.show(cards, "CreateAccount");
                }
            }
        });
        buttonPanel.add(createAccountButton);

        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "AddItem");
            }
        });
        buttonPanel.add(addItemButton);

        JButton deleteItemButton = new JButton("Delete Item");
        deleteItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int itemId = (int) model.getValueAt(selectedRow, 0);
                    inventory.removeItem(itemId);
                    refreshInventoryTable();
                } else {
                    JOptionPane.showMessageDialog(frame, "No item selected.");
                }
            }
        });
        buttonPanel.add(deleteItemButton);

        JButton performSalesButton = new JButton("Perform Sale");
        performSalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "PerformSales");
            }
        });
        buttonPanel.add(performSalesButton);

        JButton deleteUserButton = new JButton("Delete User");
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cards, "DeleteAccount");            }
        });
        buttonPanel.add(deleteUserButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setUser(null);
                frame.remove(buttonsPanel);
                cardLayout.show(cards, "Login");
            }
        });
        buttonPanel.add(logoutButton);

        JLabel usernameLabel = new JLabel();
        usernameLabel.setText(user.getUsername());
        usernameLabel.setHorizontalAlignment(JLabel.RIGHT);
        buttonPanel.add(usernameLabel, BorderLayout.EAST);

        return buttonPanel;
    }

    public JPanel createPerformSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        panel.setPreferredSize(new Dimension(800,600));
        panel.setMinimumSize(new Dimension(800,600));
        panel.setMaximumSize(new Dimension(800,600));
        String[] columns = {"ID", "Name", "Quantity", "Unit Price", "Total"};
        DefaultTableModel salesModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(salesModel);

        // Create search panel
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Item:"));
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        DefaultListModel<Item> listModel = new DefaultListModel<>();
        JList<Item> list = new JList<>(listModel);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String itemName = searchField.getText();
                ArrayList<Item> items = inventory.getInventoryItems();
                listModel.clear();
                for (Item item : items) {
                    if (item.getName().toLowerCase().contains(itemName.toLowerCase())){
                        listModel.addElement(item);
                    }
                }
                searchField.setText("");
            }
        });
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        JTextField netTotalField = new JTextField("0.00", 8);
        netTotalField.setEditable(false); // Make the field uneditable
        searchPanel.add(new JLabel("Net Total:"));
        searchPanel.add(netTotalField);

        // Right panel
        JPanel rightPanel = new JPanel();

        JButton addButton = new JButton("Add");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Item item = list.getSelectedValue();
                if (item != null) {
                    // getting current quantity
                    int quantity = (int) quantitySpinner.getValue();
                    double price = item.getPrice();
                    double newTotal = quantity * price;

                    if (quantity > item.getQuantity()){
                        JOptionPane.showMessageDialog(frame, "Please select within available quantity");
                        return;
                    }

                    boolean found = false;  // if product is found or not

                    // checking if we have already added item to list. If we have then increase
                    // quantity only
                    double netTotal = 0;
                    for (int i = 0; i < salesModel.getRowCount(); i++){
                        if ((int) salesModel.getValueAt(i, 0) == item.getId()){
                            found = true;
                            int existingQuantity = (int) salesModel.getValueAt(i, 2);

                            // verifying that selected quantity is in available quantity range
                            if (item.getQuantity() < existingQuantity + quantity){
                                JOptionPane.showMessageDialog(frame, "The requested quantity is not available.");
                                return;
                            }

                            salesModel.setValueAt(existingQuantity + quantity, i, 2);

                            double existingTotal = (double) salesModel.getValueAt(i, 4);
                            salesModel.setValueAt(existingTotal + newTotal, i, 4);
                        }

                        // finding new net total
                        netTotal += (double) salesModel.getValueAt(i, 4);
                    }

                    if (!found){
                        salesModel.addRow(new Object[]{item.getId(), item.getName(), quantity, price, newTotal});
                        netTotal += newTotal;
                    }
                    System.out.println("netTotal " + netTotal);
                    netTotalField.setText(String.valueOf(netTotal));
                }
            }
        });

        // Create perform sale button
        JButton performSaleButton = new JButton("Perform Sale");
        performSaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // if there are any products
                if (salesModel.getRowCount() > 0){
                    for (int i = 0; i < salesModel.getRowCount(); i++) {
                        int itemId = (int) salesModel.getValueAt(i, 0);
                        int quantity = (int) salesModel.getValueAt(i, 2);
                        inventory.performSale(itemId, quantity);
                    }
                    refreshInventoryTable();
                    salesModel.setRowCount(0);
                    listModel.clear();
                    netTotalField.setText("0.00");
                    JOptionPane.showMessageDialog(frame, "Sale performed successfully.");
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Please select an item");
                }
            }
        });
        rightPanel.add(quantitySpinner);
        rightPanel.add(addButton);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.WEST);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);
        panel.add(performSaleButton, BorderLayout.SOUTH);

        return panel;
    }

    public JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 4, 10)); // Increased gap between components
        panel.setPreferredSize(new Dimension(400,400));
        panel.setMaximumSize(new Dimension(400,400));
        panel.setMinimumSize(new Dimension(400,400));
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new GridLayout(1, 1));

        usernamePanel.add(new JLabel("       Username:"));
        JTextField usernameField = new JTextField(15);
        // Set preferred size for the text field
        usernameField.setPreferredSize(new Dimension(20, 30)); // Decreased height
        usernamePanel.add(usernameField);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new GridLayout(1, 2, 4, 4));
        passwordPanel.add(new JLabel("       Password:"));
        JPasswordField passwordField = new JPasswordField(15);
        // Set preferred size for the password field
        passwordField.setPreferredSize(new Dimension(20, 30)); // Decreased height
        passwordPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 40)); // Increased height
        buttonPanel.add(loginButton);
        panel.setPreferredSize(new Dimension(400, 400));

        panel.add(usernamePanel);
        panel.add(passwordPanel);
        panel.add(buttonPanel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                User currentUser = accounts.authenticateUser(username, password);
                if (currentUser != null){
                    usernameField.setText("");
                    passwordField.setText("");

                    setUser(currentUser);
                    buttonsPanel = createMenuButtonsPanel();
                    frame.add(buttonsPanel, BorderLayout.NORTH);
                    // Remove login panel from the frame
                    frame.remove(panel);
                    // Resize the frame to fit the menu buttons
                    frame.pack(); // Instead of adjusting height manually
                    cardLayout.show(cards, "Inventory");
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                }

            }
        });

        return panel;
    }

    public JPanel createDeleteAccountPanel() {
        JPanel panel = new JPanel();

        panel.add(new JLabel("Username:"));

        panel.setPreferredSize(new Dimension(800,600));
        panel.setMinimumSize(new Dimension(800,600));
        panel.setMaximumSize(new Dimension(800,600));
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField);

        JButton deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String username = usernameField.getText();
                String response = accounts.deleteAccount(getUser(), username);
                JOptionPane.showMessageDialog(frame, response);
                if (username.equals(getUser().getUsername())) {
                    if (response.toLowerCase().contains("success")){
                        setUser(null);
                        frame.remove(buttonsPanel);
                        // user deleted his own account
                        cardLayout.show(cards, "Login");
                    }
                }
                usernameField.setText("");
            }
        });
        panel.add(deleteAccountButton);

        return panel;
    }

    public JPanel createCreateAccountPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 4, 4));
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));
        JLabel usernamelabel =new JLabel("       Username:");
        usernamelabel.setFont(new Font("Times New Roman", Font.PLAIN, 17 ));
        panel.add(usernamelabel);

        panel.setPreferredSize(new Dimension(800,600));
        panel.setMinimumSize(new Dimension(800,600));
        panel.setMaximumSize(new Dimension(800,600));
        JTextField usernameField = new JTextField(15);
        panel.add(usernameField);
        JLabel passwordlabel=new JLabel("       Password:");
        passwordlabel.setFont(new Font("Times New Roman", Font.PLAIN, 17 ));

        panel.add(passwordlabel);
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);
        JLabel confirmpasswordlabel =new JLabel("       Confirm Password:");
        confirmpasswordlabel.setFont(new Font("Times New Roman", Font.PLAIN, 17 ));
        panel.add(confirmpasswordlabel);
        JPasswordField confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);
        JLabel rolelabel =new JLabel("       Role:");
        rolelabel.setFont(new Font("Times New Roman", Font.PLAIN, 17 ));
        panel.add(rolelabel);
        String[] roles = { Roles.CASHIER, Roles.ADMINISTRATOR };
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        panel.add(roleComboBox);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String pwd = new String(passwordField.getPassword());
                String pwdConfirm = new String(confirmPasswordField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();

                if (!getUser().isAdmin()){
                    JOptionPane.showMessageDialog(frame, "Only admin can create account");
                } else {
                    if (pwd.equals(pwdConfirm)){
                        String response = accounts.createAccount(username, pwd, role, getUser());
                        JOptionPane.showMessageDialog(frame, response);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Passwords does not match.");
                    }
                }

                confirmPasswordField.setText("");
                usernameField.setText("");
                passwordField.setText("");
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getUser() != null){
                    cardLayout.show(cards, "Inventory");
                } else {
                    cardLayout.show(cards, "Login");
                }
            }
        });

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 4, 4));
        buttonsPanel.add(createAccountButton);
        buttonsPanel.add(backButton);
        panel.add(buttonsPanel);

        return panel;
    }


    public JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(4, 4));

        panel.setPreferredSize(new Dimension(800,600));
        panel.setMinimumSize(new Dimension(800,600));
        panel.setMaximumSize(new Dimension(800,600));

        JPanel searchPanel = new JPanel();
        JLabel searchLabel = new JLabel("Search: ");

        searchPanel.add(searchLabel);
        JTextField searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                searchField.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchPanel.add(searchField);
        searchField.getDocument().addDocumentListener(new DocumentListener() {

            public void filterItems(){
                String searchTerm = searchField.getText();
                ArrayList<Item> items = inventory.getInventoryItems();
                ArrayList<Item> filteredItems = new ArrayList<Item>();
                for (Item item : items){
                    if (item.getName().toLowerCase().contains(searchTerm.toLowerCase())){
                        filteredItems.add(item);
                    }
                }
                setInventoryTable(filteredItems);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                filterItems();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterItems();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterItems();
            }
        });
        panel.add(searchPanel, BorderLayout.NORTH);

        // setting inventory data
        setInventoryTable(inventory.getInventoryItems());

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void setInventoryTable(ArrayList<Item> items) {
        String[] columns = {"Product ID", "Product Name", "Quantity", "Price"};
        Object[][] data = new Object[items.size()][4];
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            data[i][0] = item.getId();
            data[i][1] = item.getName();
            data[i][2] = item.getQuantity();
            data[i][3] = item.getPrice();
        }

        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setDataVector(data, columns);
        if (table != null) {
            table.setModel(model);
        }
    }

    private JPanel createAddItemPanel() {
        // Create a panel for the item input fields
        JPanel itemPanel = new JPanel(new GridLayout(8, 1, 4, 4));

        itemPanel.setPreferredSize(new Dimension(800,600));
        itemPanel.setMinimumSize(new Dimension(800,600));
        itemPanel.setMaximumSize(new Dimension(800,600));

        JTextField nameField = new JTextField(15);
        nameField.setFont(new Font("Times New Roman", Font.PLAIN, 15 ));
        itemPanel.add(new JLabel(""));
        itemPanel.add(new JLabel(""));
        JLabel namelabel =new JLabel("       Name:");
        namelabel.setFont(new Font("Times New Roman", Font.PLAIN, 18 ));
        itemPanel.add(namelabel);
        itemPanel.add(nameField);

        JTextField descriptionField = new JTextField(15);
        descriptionField.setFont(new Font("Times New Roman", Font.PLAIN, 15 ));
        JLabel descriptionlabel =new JLabel("       Description:");
        descriptionlabel.setFont(new Font("Times New Roman", Font.PLAIN, 18 ));

        itemPanel.add(descriptionlabel);
        itemPanel.add(descriptionField);

        JTextField categoryField = new JTextField(15);
        categoryField.setFont(new Font("Times New Roman", Font.PLAIN, 15 ));
        JLabel categorylabel = new JLabel("       Category:");
        categorylabel.setFont(new Font("Times New Roman", Font.PLAIN, 18 ));
        itemPanel.add(categorylabel);
        itemPanel.add(categoryField);

        JSpinner quantityField = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        quantityField.setFont(new Font("Times New Roman", Font.PLAIN, 15 ));
        JLabel quantitylabel =new JLabel("       Quantity:");
        quantitylabel.setFont(new Font("Times New Roman", Font.PLAIN, 18 ));
        itemPanel.add(quantitylabel);
        itemPanel.add(quantityField);

        JSpinner priceField = new JSpinner(new SpinnerNumberModel(
                1.0, 1.0, Double.MAX_VALUE, 1.0
        ));
        priceField.setFont(new Font("Times New Roman", Font.PLAIN, 15 ));
        JLabel pricelabel =new JLabel("       Price:");
        pricelabel.setFont(new Font("Times New Roman", Font.PLAIN, 18 ));
        itemPanel.add(pricelabel);
        itemPanel.add(priceField);

        // Create a button to add the item to the database
        JButton addButton = new JButton("       Add Item");
        addButton.setFont(new Font("Times New Roman", Font.PLAIN, 15 ));
        addButton.setBounds(100,0,100,50);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String description = descriptionField.getText();
                String category = categoryField.getText();
                int quantity = (int) quantityField.getValue();
                System.out.println(priceField.getValue());
                double price = (double) priceField.getValue();
                System.out.println(price);

                if (name.trim().length() == 0) {
                    JOptionPane.showMessageDialog(frame, "Please provide item name");
                    return;
                }

                Item item = new Item(name, description, category, quantity, price);
                inventory.addItem(item);

                nameField.setText("");
                categoryField.setText("");
                descriptionField.setText("");
                quantityField.setValue(1);
                priceField.setValue(1.0);

                refreshInventoryTable();
                cardLayout.show(cards, "Inventory");
            }
        });
        itemPanel.add(new JLabel(""));
        itemPanel.add(new JLabel(""));
        itemPanel.add(addButton);
        itemPanel.add(new JLabel(""));
//        itemPanel.add(new JLabel(""));
//        itemPanel.add(new JLabel(""));
//        itemPanel.add(new JLabel(""));
//        itemPanel.add(new JLabel(""));

        return itemPanel;
    }

    public static void main(String[] args) {
        new App();
    }
}