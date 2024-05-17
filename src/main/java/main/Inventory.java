package main;

import java.util.ArrayList;

public class Inventory implements IInventory{
    private Database db;
    private ArrayList<Item> items;

    public Inventory(Database db){
        this.db = db;
        this.items = getInventoryItems();
    }

    public ArrayList<Item> getInventoryItems() {
        return db.getItems();
    }

    // Get Low Stock Items (assuming a threshold for low stock)
    public ArrayList<Item> getLowStockItems(int threshold) {
        return filterLowStockItems(threshold);
    }
    // if no threshold is given suppose it is 50
    public ArrayList<Item> getLowStockItems() {
        return filterLowStockItems(50);
    }

    // Remove main.Item
    public void removeItem(int itemId) {
        // Call Database method to remove item
        db.removeItem(itemId);
    }

    public void addItem(Item item) {
        db.insertItem(item);
    }

    // Update Stock
    public void performSale(int itemId, int quantity) {
        Item item = db.retrieveItem(itemId);
        int remainingQuantity = item.getQuantity() - quantity;
        // Call main.Database method to update stock
        db.updateItemStock(itemId, remainingQuantity);
    }

    // defining function for filtering low stock items, so we can use it in both cases when
    // threshold is given or not
    public ArrayList<Item> filterLowStockItems(int threshold) {
        ArrayList<Item> lowStockItems = new ArrayList<>();
        ArrayList<Item> items = getInventoryItems();
        for (Item item : items) {
            if (item.getQuantity() < threshold) {
                lowStockItems.add(item);
            }
        }
        return lowStockItems;
    }

}
