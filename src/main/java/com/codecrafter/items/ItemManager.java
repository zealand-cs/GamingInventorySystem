package com.codecrafter.items;

import com.codecrafter.inventory.Item;

import java.util.*;

public class ItemManager {
    final static ItemManager instance = new ItemManager();

    public static ItemManager getInstance() {
        return instance;
    }

    private List<Item> items = new ArrayList<>();

    public void insertItem(Item item) {
        items.add(item);
    }

    public List<Item> getItems() {
        return items;
    }
}
