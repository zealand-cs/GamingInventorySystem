package com.codecrafter.items;

import com.codecrafter.inventory.Item;

import java.util.*;

/**
 * A simple class for managing available items in the program, using the singleton pattern.
 */
public class ItemManager {
    /**
     * The singleton instance.
     */
    final static ItemManager instance = new ItemManager();

    /**
     * @return the singleton instance
     */
    public static ItemManager getInstance() {
        return instance;
    }

    /**
     * A list of all available items.
     */
    private final List<Item> items = new ArrayList<>();

    /**
     * Inserts a new item to the manager, making it available to the entire program
     * @param item the new item to insert
     */
    public void insertItem(Item item) {
        items.add(item);
    }

    /**
     * @return a list of all available items in the program.
     */
    public List<Item> getItems() {
        return items;
    }
}
