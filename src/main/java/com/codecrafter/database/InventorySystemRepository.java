package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;

import java.util.List;

public interface InventorySystemRepository {
    List<Inventory> getInventories();

    Inventory newInventory(String name, int unlockedSlots);
    void saveInventory(Inventory inventory);
    void deleteInventory(Inventory inventory);

    List<Item> getItems();
}
