package com.codecrafter.inventory;

/**
 * Defines how to sort an inventory
 */
public enum SortValue {
    /**
     * Sorts based on item id
     */
    Id,

    /**
     * Sorts based on item name
     */
    Alphabetical,

    /**
     * Sorts based on item type
     */
    ItemType,

    /**
     * Sorts based on slot weight
     */
    Weight,
}

