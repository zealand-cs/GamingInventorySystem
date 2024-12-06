package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;

import java.util.List;

public interface InventorySystemRepository {
    List<Inventory> getInventories();

    Inventory newInventory(String name, int unlockedSlots);
    void save();
    void addInventory(Inventory inventory);
    void removeInventory(Inventory inventory);

    List<Item> getItems();
}
