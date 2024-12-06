package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

public class ThrowingWeapon extends Item {
    private WeaponHandedness weaponHandedness;
    private double damage;
    private int durability;
    private double throwingDistance;

    public ThrowingWeapon() { }

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
