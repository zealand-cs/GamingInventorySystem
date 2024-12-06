package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

public class ArmorItem extends Item {
    public ArmorItem() { }

    public ArmorItem(int id, ItemType type, String name, double weight, int maxStack) {
        super(id, type, name, weight, maxStack);
    }

    @Override
    protected void use() {

    }
}
