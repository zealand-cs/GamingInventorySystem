package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

/**
 * A potion item that extends Item.
 */
public class ConsumablePotion extends Item {
    /**
     * Empty constructor for json serialization and deserialization
     */
    private ConsumablePotion() { }

    public ConsumablePotion(int id, String name, double weight, int maxStack) {
        super(id, ItemType.ConsumablePotion, name, weight, maxStack);
    }

    @Override
    protected void use() {

    }
}
