package com.codecrafter.inventory;

import com.codecrafter.items.ArmorItem;
import com.codecrafter.items.ConsumablePotion;
import com.codecrafter.items.ThrowingWeapon;
import com.codecrafter.items.WeaponItem;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
    private int id;
    private ItemType type;
    private String name;
    private double weight;
    private int maxStack;

    // Private empty constructor for JSON reading
    protected Item() { }

    public Item(int id, ItemType type, String name, double weight, int maxStack) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.weight = weight;
        this.maxStack = maxStack;
    }

    protected abstract void use();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }
}
