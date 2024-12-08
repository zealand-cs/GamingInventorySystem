package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class InventoryFile {
    private final List<Inventory> inventories;

    public InventoryFile() {
        inventories = new ArrayList<>();
    }

    void addInventory(Inventory inventory) {
        inventories.add(inventory);
    }

    void removeInventory(Inventory inventory) {
        inventories.remove(inventory);
    }

    public List<Inventory> getInventories() {
        return inventories;
    }

    public int inventoryCount() {
        return inventories.size();
    }
}

public class FileRepository implements InventorySystemRepository {
    String fileName;
    InventoryFile inventoryFile;

    public FileRepository(String fileName) throws MalformedFileException {
        this.fileName = fileName;
        this.inventoryFile = readInventoryFile();
    }

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

    @Override
    public void save() {
        // Inventory already in inventory list, so just write file again
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

