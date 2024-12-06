package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

public class ConsumablePotion extends Item {
    public ConsumablePotion() { }

    public ConsumablePotion(int id, String name, double weight, int maxStack) {
        super(id, ItemType.ConsumablePotion, name, weight, maxStack);
    }

    @Override
    protected void use() {

    }
}
