package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

/**
 * An armor item that extends Item.
 */
public class ArmorItem extends Item {
    /**
     * Empty constructor for json serialization and deserialization
     */
    private ArmorItem() { }

    public ArmorItem(int id, String name, double weight, int maxStack) {
        super(id, ItemType.Armor, name, weight, maxStack);
    }

    @Override
    protected void use() {

    }
}
