package com.codecrafter.inventory;

public abstract class Item {
    int id;
    ItemType type;
    String name;
    double weight;
    int maxStack;
    Boolean consumable;

    abstract void use();

    double getWeight() {
        return weight;
    }
}
