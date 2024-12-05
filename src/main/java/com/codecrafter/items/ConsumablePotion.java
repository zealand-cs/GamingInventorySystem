package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

public class ConsumablePotion extends Item {
    public ConsumablePotion(int id, ItemType type, String name, double weight, int maxStack) {
        super(id, type, name, weight, maxStack);
    }

    @Override
    protected void use() {

    }
}
