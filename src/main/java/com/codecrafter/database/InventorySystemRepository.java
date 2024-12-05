package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;

import java.util.List;

public interface InventorySystemRepository {
    List<Inventory> getInventories();

    void saveInventory(Inventory inventory);
    void deleteInventory(int inventoryId);

    List<Item> getItems();
}
