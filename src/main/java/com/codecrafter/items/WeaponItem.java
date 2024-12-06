package com.codecrafter.items;

import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.ItemType;

public class WeaponItem extends Item {
    private WeaponHandedness weaponHandedness;
    private double damage;
    private int durability;

    public WeaponItem() { }

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

