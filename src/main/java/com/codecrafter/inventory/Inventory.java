package com.codecrafter.inventory;

public class Inventory {
    int id;
    Slot[] slots = new Slot[192];
    int unlockedSlots;

    void addItem(Item item) {

    }

    void searchInventory() {

    }

    void clearSlot(int slotNumber) {

    }

    double weight() {
        double weight = 0;
        for (var slot : slots) {
            weight += slot.weight();
        }
        return weight;
    }
}
