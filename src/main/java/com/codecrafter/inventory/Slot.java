package com.codecrafter.inventory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A slot in an inventory
 */
public class Slot {
    /**
     * The item in this slot
     */
    private Item item;
    /**
     * The number of items in this slot
     */
    private int count;

    /**
     * Constructor for an empty slot
     */
    public Slot() {
        item = null;
        count = 0;
    }

    /**
     * Sets the item in the slot
     * @param item the item to set
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * @return the item in the slot
     */
    public Item getItem() {
        return item;
    }

    /**
     * Annotated with @JsonIgnore to not write redundant data to json.
     * @return true if either the item is null or the count is 0
     */
    @JsonIgnore
    public boolean isEmpty() {
        return item == null || count <= 0;
    }

    /**
     * The reason for isEmpty() and isNotEmpty() both being implemented and ISN'T
     * just a reversal of one another is because of absolutely abysmal minimal performance gain we
     * get in the isEmpty function where the function returns early when the item is null.
     * Annotated with @JsonIgnore to not write redundant data to json.
     * @return true if the item isn't null and the count is over 0
     */
    @JsonIgnore
    public boolean isNotEmpty() {
        return item != null && count > 0;
    }

    /**
     * Sets the number of the item in this slot directly.
     * If the count is over the max item stack defined on the item, the value just get set to the max value.
     * Same principle apply when going under 0.
     * @param count the number of items to have in the inventory
     */
    public void setCount(int count) {
        if (item == null) {
            this.count = 0;
        } else {
            if (count > item.getMaxStack()) {
                this.count = item.getMaxStack();
            } else {
                this.count = Math.max(count, 0);
            }
        }
    }

    /**
     * @return the item-count in this slot
     */
    public int getCount() {
        return count;
    }

    /**
     * Annotated with @JsonIgnore to not write redundant data to json.
     * @return the total weight of the slot
     */
    @JsonIgnore
    public double getWeight() {
        if (isEmpty()) {
            return 0;
        }

        return item.getWeight() * count;
    }

    /**
     * Empties the slot entirely
     */
    public void clear() {
        item = null;
        count = 0;
    }

    /**
     * Increments the item count by one
     */
    public void incrementCount() {
        setCount(count + 1);
    }

    /**
     * Decrements the item count by one
     */
    public void decrementCount() {
        setCount(count - 1);
    }

    /**
     * Uses the item in the slot
     */
    public void use() {
        item.use();
    }
}
