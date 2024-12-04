package com.codecrafter.inventory;

public class Inventory {
    private int id;
    private String name;

    final static double MAX_WEIGHT = 50;

    public Inventory(int id, String name, int unlockedSlots) {
        this.id = id;
        this.name = name;
        this.unlockedSlots = unlockedSlots;
    }

    private Slot[] slots = new Slot[192];
    private int unlockedSlots;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnlockedSlots() {
        return unlockedSlots;
    }

    public void setUnlockedSlots(int unlockedSlots) {
        this.unlockedSlots = Math.max(Math.min(unlockedSlots, slots.length), 0);
    }

    public Slot[] getSlots() {
        return slots;
    }

    public Slot getSlot(int index) throws InvalidSlotException {
        if (index > unlockedSlots - 1 || index < 0) {
            throw new InvalidSlotException();
        }

        return slots[index];
    }

    public void insertSlot(Slot slot, int index) {
        if (getWeight() + slot.getWeight() <= MAX_WEIGHT) {
            slots[index] = slot;
        }
    }

    public void emptySlot(Slot slot) {
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] == slot) {
                slots[i] = null;
            }
        }
    }

    public double getWeight() {
        double weight = 0;
        for (var slot : slots) {
            if (slot != null) {
                weight += slot.getWeight();
            }
        }
        return weight;
    }
}

