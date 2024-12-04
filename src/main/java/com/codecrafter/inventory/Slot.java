package com.codecrafter.inventory;

public class Slot {
    private Item item;
    private int count;

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
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

    public void use() {
        item.use();
    }
}
