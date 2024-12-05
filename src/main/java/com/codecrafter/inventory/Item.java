package com.codecrafter.inventory;

public abstract class Item {
    private int id;
    private ItemType type;
    private String name;
    private double weight;
    private int maxStack;

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
