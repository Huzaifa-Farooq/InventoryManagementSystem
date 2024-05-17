package main;

import java.util.ArrayList;

public interface IInventory {
    ArrayList<Item> getInventoryItems();
    ArrayList<Item> getLowStockItems(int threshold);
    ArrayList<Item> getLowStockItems();
    void removeItem(int itemId);
    void addItem(Item item);
    void performSale(int itemId, int quantity);
    ArrayList<Item> filterLowStockItems(int threshold);
}