package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

/**
 * A weapon item that extends Item.
 */
public class WeaponItem extends Item {
    /**
     * Defines which hand the weapon ca nbe held in
     */
    private WeaponHandedness weaponHandedness;
    /**
     * The damage that the weapon does
     */
    private double damage;
    /**
     * The durbility of the item
     */
    private int durability;

    /**
     * Empty constructor for json serialization and deserialization
     */
    private WeaponItem() { }

    public WeaponItem(int id, String name, double weight, int maxStack, WeaponHandedness weaponHandedness, double damage, int durability) {
        super(id, ItemType.Weapon, name, weight, maxStack);
        this.weaponHandedness = weaponHandedness;
        this.damage = damage;
        this.durability = durability;
    }

    @Override
    protected void use() {
        durability--;
    }
}

