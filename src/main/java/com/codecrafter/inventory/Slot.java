package com.codecrafter.inventory;

public class Slot {
    Item item;
    int count;

    double weight() {
        return item.getWeight() * count;
    }
}
