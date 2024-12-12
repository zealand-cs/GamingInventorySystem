package com.codecrafter;

import com.codecrafter.database.InventorySystemRepository;
import com.codecrafter.exceptions.InvalidInputException;
import com.codecrafter.exceptions.InvalidSlotException;
import com.codecrafter.exceptions.TooMuchWeightException;
import com.codecrafter.inventory.*;
import com.codecrafter.items.ItemManager;
import com.codecrafter.items.WeaponItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Gui {
    private final InventorySystemRepository repository;
    Scanner scanner = new Scanner(System.in);

    Gui(InventorySystemRepository repository) {
        this.repository = repository;
    }

    public void start() {
        mainLoop: while (true) {
            System.out.println("[1] Select inventory");
            System.out.println("[2] New inventory");
            System.out.println("[3] Import inventory");
            System.out.println("[0] Exit");

            var inventories = repository.getInventories();

            try {
                var option = readOption(0, 3);

                Inventory inventory = null;
                switch (option) {
                    case 1 -> inventory = selectInventoryPrompt(inventories);
                    case 2 -> inventory = createInventoryPrompt();
                    case 3 -> {
                        System.out.println("Enter the filepath to the file you want to import: ");
                        var fileName = scanner.nextLine();
                        try {
                            var file = new File(fileName);

                            Inventory importedInventory = Inventory.fromFile(file);
                            var inventoryName = importedInventory.getName() + " (imported)";
                            importedInventory.setName(inventoryName);

                            repository.addInventory(importedInventory);
                            repository.save();

                            System.out.println("Imported inventory: " + inventoryName);
                            inventory = importedInventory;
                        } catch (IOException e) {
                            System.out.println("Error when reading file " + fileName + ". Is the path correct? Is the file valid?");
                        }
                    }
                    default -> {
                        break mainLoop;
                    }
                };

                if (inventory != null) {
                    manageInventoryPrompt(inventory);
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid option");
            }
        }
    }

    private Inventory createInventoryPrompt() {
        System.out.print("Name of the new inventory: ");

        var name = scanner.nextLine();

        return repository.newInventory(name.trim(), 32);
    }

    private Inventory selectInventoryPrompt(List<Inventory> inventories) {
        while (true) {
            int offset = 1;

            for (int i = 0; i < (long) inventories.size(); i++) {
                System.out.println("[" + (i + offset) + "] " + inventories.get(i).getName());
            }

            if (inventories.isEmpty()) {
                System.out.println("No inventories exist");
            }

            System.out.println();
            System.out.println("[0] Back");

            try {
                var option = readOption(0, inventories.size() + offset);
                if (option == 0) {
                    return null;
                }
                return inventories.get(option - offset);
            } catch (InvalidInputException e) {
                System.out.println("Invalid inventory or option");
            }
        }
    }

    private void manageInventoryPrompt(Inventory inventory) {
        inventoryLoop: while(true) {
            printInventoryStats(inventory);

            System.out.print("[1] Show slots ");
            System.out.print("[2] Rename inventory ");
            System.out.print("[3] Export inventory ");
            System.out.println("[4] Delete inventory ");
            System.out.println("[0] Save and leave inventory");

            try {
                var option = readOption(0, 4);

                switch (option) {
                    case 0 -> {
                        repository.save();
                        break inventoryLoop;
                    }
                    case 1 -> manageSlotsPrompt(inventory);
                    case 2 -> {
                        System.out.println("Enter new name of the inventory (old: " + inventory.getName() + ")");
                        var newName = scanner.nextLine();
                        inventory.setName(newName.trim());
                        repository.save();
                    }
                    case 3 -> {
                        System.out.println("Input file path to export to: ");
                        var path = scanner.nextLine();
                        try {
                            var file = new FileWriter(path, false);

                            inventory.writeToFile(file);
                        } catch (IOException e) {
                            System.out.println("Error when exporting the inventory. Try another path.");
                        }
                    }
                    case 4 -> {
                        repository.removeInventory(inventory);
                        repository.save();

                        System.out.println("Deleted inventory");
                        break inventoryLoop;
                    }
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid input option");
            }
        }
    }

    private static void printInventoryStats(Inventory inventory) {
        System.out.println("Name: " + inventory.getName());
        System.out.println("Weight: " + inventory.getWeight());
        System.out.println("Unlocked slots: " + inventory.getUnlockedSlots());
        System.out.println();
    }

    /// Prints all inventory slots in the terminal shown as options. Starting at selection index startIndex.
    ///
    /// Returns an int which is the latest index showed
    private int printInventorySlots(Inventory inventory, int offset) {
        int rowLength = 8;

        var slots = inventory.getSlots();

        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];

            if (i != 0 && i % rowLength == 0) {
                System.out.println("|");
            }

            var visualIndex = i + offset;
            if (slot.isNotEmpty()) {
                System.out.print("| [" + visualIndex + "] " + slot.getItem().getName() + " (" + slot.getCount() + ") ");
            } else {
                System.out.print("| [" + visualIndex + "] empty ");
            }
        }

        System.out.println("|");

        return inventory.getUnlockedSlots() - 1 + offset;
    }

    private void manageSlotsPrompt(Inventory inventory) {
        while (true) {
            var inventoryIndexOffset = 1;
            var latestOption = printInventorySlots(inventory, inventoryIndexOffset);

            System.out.println("[" + (latestOption + 1) + "] Sort");
            System.out.println("[0] Back");

            try {
                var option = readOption(0, latestOption + 2);

                if (option == 0) {
                    break;
                } else if (option == latestOption + 1) {
                    sortSlots(inventory);
                } else {
                    manageSlot(inventory, option - inventoryIndexOffset);
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid slot or option selected");
            }
        }
    }

    private void sortSlots(Inventory inventory) {
        while (true) {
            System.out.println("Choose parameter to sort on: ");
            System.out.print("[1] Id ");
            System.out.print("[2] Name ");
            System.out.print("[3] Item type ");
            System.out.println("[4] Weight ");
            System.out.println("[0] Cancel");

            try {
                var option = readOption(0, 4);

                switch (option) {
                    case 0 -> { /* Continues without doing anything */ }
                    case 1 -> inventory.sortInventory(SortValue.Id);
                    case 2 -> inventory.sortInventory(SortValue.Alphabetical);
                    case 3 -> inventory.sortInventory(SortValue.ItemType);
                    case 4 -> inventory.sortInventory(SortValue.Weight);
                }

                break;
            } catch (InvalidInputException e) {
                System.out.println("Invalid option selected");
            }
        }
    }


    private void manageSlot(Inventory inventory, int slotIndex) throws InvalidInputException {
        manageSlotLoop: while (true) {
            Slot slot = null;
            try {
                slot = inventory.getSlot(slotIndex);
            } catch (InvalidSlotException e) {
                throw new InvalidInputException();
            }

            if (slot.isEmpty()) {
                System.out.println("Slot is empty");
                System.out.println("[1] Insert item");
                System.out.println("[0] Back");

                try {
                    var option = readOption(0, 1);

                    switch (option) {
                        case 0 -> { break manageSlotLoop; }
                        case 1 -> insertItemToSlot(inventory, slotIndex);
                    }
                } catch (InvalidInputException e) {
                    System.out.println("Invalid input");
                }
            } else {
                System.out.println("Item: " + slot.getItem().getName() + "(id: " + slot.getItem().getId() + ")");
                System.out.println("Stack size: " + slot.getCount());
                System.out.println("Weight: " + slot.getWeight());
                System.out.println();

                System.out.print("[1] Use item ");
                System.out.print("[2] Increment ");
                System.out.print("[3] Decrement ");
                System.out.print("[4] Remove item ");
                System.out.println("[5] Swap item");
                System.out.println("[0] Back");

                try {
                    var option = readOption(0, 5);

                    // Handle selection
                    switch (option) {
                        case 0 -> { break manageSlotLoop; }
                        case 1 -> slot.use();
                        case 2 -> {
                            try {
                                inventory.incrementSlot(slotIndex);
                            } catch (TooMuchWeightException e) {
                                System.out.println("Can't add anymore to inventory: too much weight");
                            }
                        }
                        case 3 -> inventory.decrementSlot(slotIndex);
                        case 4 -> inventory.clearSlot(slotIndex);
                        case 5 -> { swapSlots(inventory, slotIndex); break manageSlotLoop; }
                    }
                } catch (InvalidInputException e) {
                    System.out.println("Invalid input");
                } catch (InvalidSlotException e) {
                    // This branch shouldn't really be possible, but we're handling it anyway
                    System.out.println("Invalid slot selected");
                }
            }
        }
    }

    private void insertItemToSlot(Inventory inventory, int slotIndex) {
        while (true) {
            var items = ItemManager.getInstance().getItems();
            System.out.println("Select item to insert");

            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                System.out.println("[" + i + "] " + item.getName());
            }

            try {
                var option = readOption(0, items.size() - 1);

                Item item = items.get(option);
                inventory.insertToSlot(slotIndex, item, 1);

                break;
            } catch (InvalidInputException e) {
                System.out.println("Invalid item selected");
            } catch (InvalidSlotException e) {
                // This branch shouldn't really be possible, but we're handling it anyway
                System.out.println("Invalid slot selected");
            } catch (TooMuchWeightException e) {
                System.out.println("Can't add anymore to inventory: too much weight");
            }
        }
    }

    private void swapSlots(Inventory inventory, int slot1) {
        while (true) {
            System.out.println("Select slot to swap with");

            var inventoryIndexOffset = 1;
            var latest = printInventorySlots(inventory, inventoryIndexOffset);
            System.out.println("[0] Cancel");

            try {
                var inputOption = readOption(0, latest + 1);

                if (inputOption != 0) {
                    inventory.swapSlots(slot1, inputOption - inventoryIndexOffset);
                    System.out.println("Swapped slots");
                    break;
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid option");
            }

        }
    }

    /// Read a number input between min (inclusive) and max (inclusive).
    ///
    /// If the number is not in this range, invalidInputText is printed, and you
    /// get to choose again
    private int readOption(int min, int max) throws InvalidInputException {
        return readOption(scanner, min, max);
    }

    private int readOption(int[] allowed) throws InvalidInputException {
        return readOption(scanner, allowed);
    }

    public static int readOption(Scanner scanner, int min, int max) throws InvalidInputException {
        return readOption(scanner, IntStream.rangeClosed(min, max).toArray());
    }

    /// Read a number input in an array that's allowed
    public static int readOption(Scanner scanner, int[] allowed) throws InvalidInputException {
        try {
            var option = scanner.nextInt();
            scanner.nextLine();

            // Return value if it matches any allowed inputs
            if (IntStream.of(allowed).anyMatch(x -> x == option)) {
                return option;
            }

            throw new InvalidInputException();
        } catch (InputMismatchException e) {
            scanner.next();
            throw new InvalidInputException();
        }
    }
}

