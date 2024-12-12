package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;

import java.util.List;

/**
 * An interface to implement data retrieval of inventories from external sources
 */
public interface InventorySystemRepository {
    /**
     * Get all available inventories
     * @return a list of all given inventories
     */
    List<Inventory> getInventories();

    /**
     * Creates a new inventory in the source. Acts kind of like a constructor for a inventory, to correctly
     * handle new inventories in the source data.
     * @param name the name of the new inventory
     * @param unlockedSlots the number of unlocked slots in the new inventory.
     * @return a new, empty inventory
     */
    Inventory newInventory(String name, int unlockedSlots);

    /**
     * Saves all modifications made in the inventory references
     */
    void save();

    /**
     * Adds an existing inventory to the source
     * @param inventory the inventory to add
     */
    void addInventory(Inventory inventory);

    /**
     * Removes an existing inventory to the source
     * @param inventory a reference to the inventory to remove
     */
    void removeInventory(Inventory inventory);
}
