package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;

import java.util.List;

public interface InventorySystemRepository {
    List<Inventory> getInventories();

    void saveInventory(Inventory inventory);
    void deleteInventory(int inventoryId);
}
