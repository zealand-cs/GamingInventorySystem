package com.codecrafter.items;

/**
 * An enum describing which hand or hands it can be held in.
 */
public enum WeaponHandedness {
    /**
     * Describes that a weapon can only be held in the main hand.
     */
    MainHand,
    /**
     * Describes that a weapon can only be held in the offhand.
     */
    Offhand,
    /**
     * Descrbies that a weapon can be held in both the main hand and offhand.
     */
    BothHands,
    /**
     * Descrbies that a weapon can only be held if both hands are available.
     */
    TwoHand
}
