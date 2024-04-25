import java.util.ArrayList;

public class Inventory {
    private Database db;
    private ArrayList<Item> items;

    public Inventory(Database db){
        this.db = db;
        this.items = db.getItems();
    }

    // Get Low Stock Items (assuming a threshold for low stock)
    public ArrayList<Item> getLowStockItems(int threshold) {
        return filterLowStockItems(threshold);
    }
    // if no threshold is given suppose it is 50
    public ArrayList<Item> getLowStockItems() {
        return filterLowStockItems(50);
    }

    // Remove Item
    public void removeItem(Item item) {
        items.remove(item);
        // Call Database method to remove item
        db.removeItem(item);
    }

    // Update Stock (assuming quantity is updated in the Item object)
    public void updateStock(Item item) {
        int index = items.indexOf(item);
        items.set(index, item); // Update item in the list
        // Call Database method to update stock (if Database supports stock updates)
        db.updateItemStock(item);
    }

    public void performSale(Item item, int quantity) {
        item.decreaseQuantity(quantity); // Update quantity in the Item object
        updateStock(item); // Update stock in the list and database
    }

    // defining function for filtering low stock items so we can use it in both cases when
    // threshold is given or not
    public ArrayList<Item> filterLowStockItems(int threshold) {
        ArrayList<Item> lowStockItems = new ArrayList<>();
        for (Item item : items) {
            if (item.getQuantity() < threshold) {
                lowStockItems.add(item);
            }
        }
        return lowStockItems;
    }

}
