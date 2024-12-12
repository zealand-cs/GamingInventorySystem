package com.codecrafter.database;

import com.codecrafter.exceptions.MalformedFileException;
import com.codecrafter.inventory.Inventory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains all data that exists in the saved file
 */
class InventoryFile {
    /**
     * A list of all loaded inventories
     */
    private final List<Inventory> inventories;

    /**
     * Constructor for creating a new file
     */
    public InventoryFile() {
        inventories = new ArrayList<>();
    }

    /**
     * Adds an inventory to the file
     * @param inventory the inventory to add
     */
    void addInventory(Inventory inventory) {
        inventories.add(inventory);
    }

    /**
     * Removes an inventory from the file½
     * @param inventory the inventory to remove
     */
    void removeInventory(Inventory inventory) {
        inventories.remove(inventory);
    }

    /**
     * Gets all inventories
     * @return a list of all loaded inventories
     */
    public List<Inventory> getInventories() {
        return inventories;
    }
}

/**
 * An implementation of InventorySystemRepository to save to a file
 */
public class FileRepository implements InventorySystemRepository {
    /**
     * The filename or path to the file
     */
    String fileName;
    /**
     * The data that's loaded, to be saved and/or modified
     */
    InventoryFile inventoryFile;

    /**
     * @param fileName the filename of the file to use
     * @throws MalformedFileException if the file is formatted incorrectly, e.g. invalid json or wrong types.
     * This should only be thrown when the file has been modified from the outside.
     */
    public FileRepository(String fileName) throws MalformedFileException {
        this.fileName = fileName;
        this.inventoryFile = readInventoryFile();
    }

    /**
     * Read the file that contains app-data
     * @return an inventory file with all inventories in the file
     * @throws MalformedFileException if the file is formatted incorrectly, e.g. invalid json or wrong types.
     * This should only be thrown when the file has been modified from the outside.
     */
    InventoryFile readInventoryFile() throws MalformedFileException {
        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                return new InventoryFile();
            } else {
                var mapper = new ObjectMapper();
                return mapper.readValue(file, InventoryFile.class);
            }
        } catch (IOException e) {
            throw new MalformedFileException();
        }
    }

    @Override
    public List<Inventory> getInventories() {
        return inventoryFile.getInventories();
    }

    @Override
    public Inventory newInventory(String name, int unlockedSlots) {
        var inventory = new Inventory(name, unlockedSlots);
        inventoryFile.addInventory(inventory);

        save();

        return inventory;
    }

    /**
     * Writes all changes in the inventory file to the disk
     */
    @Override
    public void save() {
        try (FileWriter file = new FileWriter(fileName, false)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, inventoryFile);
        } catch (IOException e) {
            System.out.println("Something went wrong while saving...");
        }
    }

    public void addInventory(Inventory inventory) {
        inventoryFile.addInventory(inventory);
    }

    @Override
    public void removeInventory(Inventory inventory) {
        inventoryFile.removeInventory(inventory);
    }
}

