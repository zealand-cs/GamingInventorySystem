package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

/**
 * A throwindweapon item that extends Item.
 */
public class ThrowingWeapon extends Item {
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
     * The distance the throwing weapon can be thrown
     */
    private double throwingDistance;

    /**
     * Empty constructor for json serialization and deserialization
     */
    private ThrowingWeapon() { }

    public ThrowingWeapon(int id, String name, double weight, int maxStack, WeaponHandedness weaponHandedness, double damage, int durability, double throwingDistance) {
        super(id, ItemType.ThrowingWeapon, name, weight, maxStack);
        this.weaponHandedness = weaponHandedness;
        this.damage = damage;
        this.durability = durability;
        this.throwingDistance = throwingDistance;
    }

    @Override
    protected void use() {

    }
}
