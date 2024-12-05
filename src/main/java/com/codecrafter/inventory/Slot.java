package com.codecrafter.inventory;

public class Slot {
    private Item item;
    private int count;

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
        } else {
            this.count = Math.max(count, 0);
        }
    }

    public int getCount() {
        return count;
    }

    public double getWeight() {
        if (isEmpty()) {
            return 0;
        }

        return item.getWeight() * count;
    }

    public void clear() {
        item = null;
        count = 0;
    }

    public void incrementCount() {
        setCount(count + 1);
    }

    public void decrementCount() {
        setCount(count - 1);
    }

    public void use() {
        item.use();
    }
}
