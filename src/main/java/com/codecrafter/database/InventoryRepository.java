package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;

import java.util.List;

public interface InventoryRepository {
    List<Inventory> getInventories();
    Inventory getInventory(int id);

    void addItemToInventory(int inventoryId, Item item);
    void removeInventorySlot(int inventoryId, int slotId);
    void getInventorySlots();
}
