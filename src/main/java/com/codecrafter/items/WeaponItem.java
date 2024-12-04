package com.codecrafter.items;

import com.codecrafter.inventory.Item;

public class WeaponItem extends Item {
    private double damage;
    private int durability;

    @Override
    protected void use() {
        durability--;
    }
}

enum WeaponType {
    // Only usable in main hand
    MainHand,
    // Only usable in offhand
    Offhand,
    // Usable in both hands
    BothHands,
    // Only usable WITH both hands
    TwoHand
}