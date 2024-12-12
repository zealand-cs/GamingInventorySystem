package com.codecrafter.inventory;

import com.codecrafter.exceptions.InvalidSlotException;
import com.codecrafter.exceptions.TooMuchWeightException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Comparator;

/**
 * The primary class to manage and manipulate an inventory.
 * The inventory is designed able to be converted into a JSON string and deserialized with
 * the help of the Jackson library
 */
public class Inventory {
    /**
     * Name of the inventory.
     * Primarily used as a human-readable way of distinguishing this inventory from others.
     */
    private String name;

    /**
     * All slots that an inventory is able to have.
     */
    private final Slot[] slots = new Slot[192];
    /**
     * The number of slots that are unlocked. If this is less than the total length
     * of the slots variable, only those fields will be available through the getter.
     */
    private int unlockedSlots;

    /**
     * The max-weight that an inventory can carry.
     * Currently, it is not possible to modify this parameter.
     */
    final static double MAX_WEIGHT = 50;

    /**
     * Private empty constructor for Jackson to properly convert JSON to objects
     */
    private Inventory() { }

    /**
     * The public constructor of an inventory
     * @param name The name of the inventory
     * @param unlockedSlots The number of unlocked slots
     */
    public Inventory(String name, int unlockedSlots) {
        this.name = name;
        this.unlockedSlots = unlockedSlots;

        // Initialize all slots, so it's not just null values
        for (int i = 0; i < slots.length; i++) {
            slots[i] = new Slot();
        }
    }

    /**
     * @return the name of the inventory
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the inventory
     * @param name new name of the inventory
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the number of unlocked slots in this inventory
     */
    public int getUnlockedSlots() {
        return unlockedSlots;
    }

    /**
     * Sets the number of unlocked slots in this inventory
     * Be careful with this, since items in slots outside of this range is unavailable.
     * @param unlockedSlots the new number of unlocked slots
     */
    public void setUnlockedSlots(int unlockedSlots) {
        this.unlockedSlots = Math.max(Math.min(unlockedSlots, slots.length), 0);
    }

    /**
     * @return an array of slots of all unlocked slots, meaning from index 0 to the number of unlocked slots.
     */
    public Slot[] getSlots() {
        // Note that this functions to is exclusive
        return Arrays.copyOfRange(slots, 0, unlockedSlots);
    }

    /**
     * Get a specific slot from the inventory
     * @param index the index of the slot
     * @return a reference to the given slot
     * @throws InvalidSlotException when index is either negative, or over `unlockedSlots`
     */
    public Slot getSlot(int index) throws InvalidSlotException {
        try {
            return getSlots()[index];
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidSlotException();
        }
    }

    /**
     * Inserts an item into a specific slot
     * @param slotIndex the slot-index to insert the item into
     * @param item the item to be inserted
     * @param count the number of the given item to be inserted
     * @throws TooMuchWeightException if the newly added item and the number of it exceeds the max weight
     * @throws InvalidSlotException if an invalid slot-index is given
     */
    public void insertToSlot(int slotIndex, Item item, int count) throws TooMuchWeightException, InvalidSlotException {
        Slot slot = getSlot(slotIndex);

        if (slot != null) {
            if (getWeight() + (item.getWeight() * count) > MAX_WEIGHT) {
                throw new TooMuchWeightException();
            }

            slot.setItem(item);
            slot.setCount(count);
        }
    }

    /**
     * Increments the item-count in a given slot by one
     * @param slotIndex the index of the slot
     * @throws TooMuchWeightException if the incremented item-slot exceeds the max weight of the inventory
     * @throws InvalidSlotException if an invalid slot-index is given
     */
    public void incrementSlot(int slotIndex) throws TooMuchWeightException, InvalidSlotException {
        Slot slot = getSlot(slotIndex);
        if (slot != null) {
            if (getWeight() + slot.getItem().getWeight() > MAX_WEIGHT) {
                throw new TooMuchWeightException();
            }

            slot.incrementCount();
        }
    }

    /**
     * Decrements the item-count in a given slot by one
     * No exceptions is thrown here since
     * @param slotIndex the index of the slot
     * @throws InvalidSlotException if an invalid slot-index is given
     */
    public void decrementSlot(int slotIndex) throws InvalidSlotException {
        Slot slot = getSlot(slotIndex);
        if (slot != null) {
            slot.decrementCount();
        }
    }

    /**
     * Clears a given slot
     * @param slotIndex the index of the slot
     * @throws InvalidSlotException if an invalid slot-index is given
     */
    public void clearSlot(int slotIndex) throws InvalidSlotException {
        Slot slot = getSlot(slotIndex);
        if (slot != null) {
            slot.clear();
        }
    }

    /**
     * Get the total weight of the inventory
     * Tagged with @JsonIgnore to not be included in the JSON-output since it's dynamic
     * @return the total weight of the inventory
     */
    @JsonIgnore
    public double getWeight() {
        double weight = 0;
        for (var slot : getSlots()) {
            if (slot != null) {
                weight += slot.getWeight();
            }
        }
        return weight;
    }

    /**
     * Sorts the inventory
     * @param sort how to sort the slots
     */
    public void sortInventory(SortValue sort) {
        switch (sort) {
            case SortValue.Id -> Arrays.sort(slots, new SortById());
            case SortValue.Alphabetical -> Arrays.sort(slots, new SortByAlphabetical());
            case SortValue.ItemType -> Arrays.sort(slots, new SortByItemType());
            case SortValue.Weight -> Arrays.sort(slots, new SortByWeight());
        }
    }

    /**
     * Swaps two slots.
     * This function doesn't care in which direction these are inputted, since the result is the same anyway.
     * @param slot1 the first slot
     * @param slot2 the second slot
     */
    public void swapSlots(int slot1, int slot2) {
        Slot tmp = slots[slot1];
        slots[slot1] = slots[slot2];
        slots[slot2] = tmp;
    }

    /**
     * Writes the inventory to a file.
     * @param w a writer
     * @throws IOException if something goes wrong in the conversion process.
     */
    public void writeToFile(Writer w) throws IOException {
        var mapper = new ObjectMapper();
        mapper.writeValue(w, this);
    }

    /**
     * Creates an inventory from a json-file.
     * @param file the file to read
     * @return a new inventory instance
     * @throws IOException if something goes wrong in the deserialization process
     */
    public static Inventory fromFile(File file) throws IOException {
        var mapper = new ObjectMapper();
        return mapper.readValue(file, Inventory.class);
    }
}

/**
 * A sorting implementation to sort slots by item id
 */
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

/**
 * A sorting implementation to sort slots by item name
 */
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

/**
 * An implementation to sort slots by item type
 */
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

/**
 * An implementation to sort slots by slot weight
 */
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

