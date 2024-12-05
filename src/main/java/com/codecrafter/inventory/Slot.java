package com.codecrafter.inventory;

public class Slot {
    private Item item;
    private int count = 0;

    public Slot() {
        item = null;
        count = 0;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean isEmpty() {
        return count <= 0 || item == null;
    }

    public boolean isNotEmpty() {
        return item != null && count > 0;
    }

    public void setCount(int count) {
        if (count > item.getMaxStack()) {
            this.count = item.getMaxStack();
        } else if (count < 0) {
            this.count = 1;
        } else {
            this.count = count;
        }
    }

    public int getCount() {
        return count;
    }

    public double getWeight() {
        return item.getWeight() * count;
    }

    public void clear() {
        item = null;
        count = 0;
    }

    public void use() {
        item.use();
    }
}
