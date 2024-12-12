package com.codecrafter.inventory;

import com.codecrafter.items.ArmorItem;
import com.codecrafter.items.ConsumablePotion;
import com.codecrafter.items.ThrowingWeapon;
import com.codecrafter.items.WeaponItem;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An abstract class for different items
 * Here we also define how Jackson should handle the json conversion to the correct types.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @Type(value = ArmorItem.class, name = "Armor"),
        @Type(value = ConsumablePotion.class, name = "ConsumablePotion"),
        @Type(value = ThrowingWeapon.class, name = "ThrowingWeapon"),
        @Type(value = WeaponItem.class, name = "Weapon"),
})
public abstract class Item {
    /**
     * The internal id of the item
     */
    private int id;
    /**
     * The type of item. This is also described by the type itself, but this helps
     * us convert correctly when reading the item from json.
     */
    private ItemType type;
    /**
     * The name of the item
     */
    private String name;
    /**
     * The weight of the item
     */
    private double weight;
    /**
     * The max stack this item allows
     */
    private int maxStack;

    /**
     * Private empty constructor for JSON reading
     */
    protected Item() { }

    /**
     * The base constructor of the item. Helps extend the abstract class.
     * @param id the id of the item
     * @param type the type of the item
     * @param name the name of the item
     * @param weight the weight of a single one of this item
     * @param maxStack the max allowed stack of this item
     */
    public Item(int id, ItemType type, String name, double weight, int maxStack) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.weight = weight;
        this.maxStack = maxStack;
    }

    /**
     * Allows us to define some custom logic on different types of items
     * This really isn't implemented yet, it's here to show that we
     * know how it could be done.
     */
    protected abstract void use();

    /**
     * @return the id of the item
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the item. Should really not be called, but you still have the option,
     * since it wouldn't break anything.
     * @param id new id of the item
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the type of the item
     */
    public ItemType getType() {
        return type;
    }

    /**
     * Sets the type of the item. Should really not be called, but you still have the option,
     * since it wouldn't break anything.
     * @param type new type of the item
     */
    public void setType(ItemType type) {
        this.type = type;
    }

    /**
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item. Should really not be called, but you still have the option,
     * since it wouldn't break anything.
     * @param name the new name of the item
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the weight of the item
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Set the weight of the item.
     * @param weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the max stack this item allows
     */
    public int getMaxStack() {
        return maxStack;
    }

    /**
     * Sets the max allowed stack for the item. Should really not be called, but you still have the option,
     * since it wouldn't break any logic.
     * @param maxStack the new max stack for the item
     */
    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }
}
