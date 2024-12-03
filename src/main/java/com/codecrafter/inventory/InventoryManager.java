package com.codecrafter.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InventoryManager {
    static InventoryManager instance = new InventoryManager();

    List<Inventory> inventories = new ArrayList<>();

    public static InventoryManager getInstance() {
        return instance;
    }

    Inventory newInventory() {
        Inventory inventory = new Inventory();
        inventories.add(inventory);
        return inventory;
    }

    void importInventory() {

    }

    Optional<Inventory> getInventory(int id) {
        for (var inventory : inventories) {
            if (inventory.id == id) {
                return Optional.of(inventory);
            }
        }

        return Optional.empty();
    }
}

