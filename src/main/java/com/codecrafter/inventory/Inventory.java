package com.codecrafter.inventory;

import com.codecrafter.exceptions.InvalidSlotException;
import com.codecrafter.exceptions.TooMuchWeightException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.Comparator;

public class Inventory {
    private int id;
    private String name;

    private final Slot[] slots = new Slot[192];
    private int unlockedSlots;

    final static double MAX_WEIGHT = 50;

    // Private constructor for JSON reading
    private Inventory() { }

    public Inventory(int id, String name, int unlockedSlots) {
        this.id = id;
        this.name = name;
        this.unlockedSlots = unlockedSlots;

        for (int i = 0; i < slots.length; i++) {
            slots[i] = new Slot();
        }
    }

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
        return Arrays.copyOfRange(slots, 0, unlockedSlots);
    }

    public Slot getSlot(int index) throws InvalidSlotException {
        if (index > unlockedSlots - 1 || index < 0) {
            throw new InvalidSlotException();
        }

        return slots[index];
    }

    public void insertToSlot(int slotIndex, Item item, int count) throws TooMuchWeightException {
        Slot slot = slots[slotIndex];

        if (slot != null) {
            if (getWeight() + (item.getWeight() * count) > MAX_WEIGHT) {
                throw new TooMuchWeightException();
            }

            slot.setItem(item);
            slot.setCount(count);
        }
    }

    public void incrementSlot(int slotIndex) throws TooMuchWeightException {
        Slot slot = slots[slotIndex];
        if (slot != null) {
            if (getWeight() + slot.getItem().getWeight() > MAX_WEIGHT) {
                throw new TooMuchWeightException();
            }

            slot.incrementCount();
        }
    }

    public void decrementSlot(int slotIndex) {
        Slot slot = slots[slotIndex];
        if (slot != null) {
            slot.decrementCount();
        }
    }

    public void clearSlot(int slotIndex) {
        Slot slot = slots[slotIndex];
        if (slot != null) {
            slot.clear();
        }
    }

    @JsonIgnore
    public double getWeight() {
        double weight = 0;
        for (var slot : slots) {
            if (slot != null) {
                weight += slot.getWeight();
            }
        }
        return weight;
    }

    public void addItem(Item item) {
        // TODO
    }

    public void sortInventory(SortValue sort) {
        switch (sort) {
            case SortValue.Id -> Arrays.sort(slots, new SortById());
            case SortValue.Alphabetical -> Arrays.sort(slots, new SortByAlphabetical());
            case SortValue.ItemType -> Arrays.sort(slots, new SortByItemType());
            case SortValue.Weight -> Arrays.sort(slots, new SortByWeight());
        }
    }

    public void swapSlots(int slot1, int slot2) {
        Slot tmp = slots[slot1];
        slots[slot1] = slots[slot2];
        slots[slot2] = tmp;
    }
}

class SortById implements Comparator<Slot> {
    @Override
    public int compare(Slot x, Slot y) {
        if (x.isEmpty() && y.isEmpty()) {
            return 0;
        } else if (x.isEmpty()) {
            return 1;
        } else if (y.isEmpty()) {
            return -1;
        }

        return x.getItem().getId() - y.getItem().getId();
    }
}

class SortByAlphabetical implements Comparator<Slot> {
    @Override
    public int compare(Slot x, Slot y) {
        if (x.isEmpty() && y.isEmpty()) {
            return 0;
        } else if (x.isEmpty()) {
            return 1;
        } else if (y.isEmpty()) {
            return -1;
        }

        return x.getItem().getName().compareTo(y.getItem().getName());
    }
}

class SortByItemType implements Comparator<Slot> {
    @Override
    public int compare(Slot x, Slot y) {
        if (x.isEmpty() && y.isEmpty()) {
            return 0;
        } else if (x.isEmpty()) {
            return 1;
        } else if (y.isEmpty()) {
            return -1;
        }

        return x.getItem().getType().ordinal() - y.getItem().getType().ordinal();
    }
}

class SortByWeight implements Comparator<Slot> {
    @Override
    public int compare(Slot x, Slot y) {
        if (x.isEmpty() && y.isEmpty()) {
            return 0;
        } else if (x.isEmpty()) {
            return 1;
        } else if (y.isEmpty()) {
            return -1;
        }

        return (int)(x.getWeight() * 100) - (int)(y.getWeight() * 100);
    }
}

