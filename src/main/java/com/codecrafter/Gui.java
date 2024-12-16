package com.codecrafter;

import com.codecrafter.database.InventorySystemRepository;
import com.codecrafter.exceptions.InvalidInputException;
import com.codecrafter.exceptions.InvalidSlotException;
import com.codecrafter.exceptions.TooMuchWeightException;
import com.codecrafter.inventory.*;
import com.codecrafter.items.ItemManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * The graphical user interface class. It's just a terminal interface.
 * Yeah. That's what we did😎
 */
public class Gui {
    /**
     * The repository for retrieving data that the GUI uses.
     */
    private final InventorySystemRepository repository;
    /**
     * A scanner to write to the terminal.
     */
    Scanner scanner = new Scanner(System.in);

    /**
     * Constructor for the Gui
     * @param repository
     */
    Gui(InventorySystemRepository repository) {
        this.repository = repository;
    }

    /**
     * Entrypoint for the graphical user interface
     */
    public void start() {
        // Always loop, and only break out when you specifically select it.
        // The loop is labeled, so we can break out of the loop in a switch expression.
        mainLoop: while (true) {
            System.out.println("[1] Select inventory");
            System.out.println("[2] New inventory");
            System.out.println("[3] Import inventory");
            System.out.println("[0] Exit");

            var inventories = repository.getInventories();

            // Handle the selected option and return the final inventory
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

    /**
     * A prompt for creating a new inventory.
     * @return a new inventory instance
     */
    private Inventory createInventoryPrompt() {
        System.out.print("Name of the new inventory: ");

        var name = scanner.nextLine();

        return repository.newInventory(name.trim(), 32);
    }

    /**
     * A prompt for selecting which inventory to modify or manage
     * @param inventories all valid inventories you can select
     * @return the selected inventory
     */
    private Inventory selectInventoryPrompt(List<Inventory> inventories) {
        // Loops until a specific inventory is returned from the function.
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

            // Handle input. The loop will make it retry if something is wrong.
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

    /**
     * The prompt for managing an inventory
     * @param inventory the inventory to manage and see
     */
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

    /**
     * Prints stats about an inventory
     * @param inventory the inventory to print stats about
     */
    private static void printInventoryStats(Inventory inventory) {
        System.out.println("Name: " + inventory.getName());
        System.out.println("Weight: " + inventory.getWeight());
        System.out.println("Unlocked slots: " + inventory.getUnlockedSlots());
        System.out.println();
    }

    /**
     * Prints all inventory slots in the terminal shown as options. Starting at selection index startIndex.
     * @param inventory the inventory to print slots from
     * @param offset the shown starting index offset
     * @return an int which is the latest index showed.
     */
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

    /**
     * A prompt for managing different slots in the inventory
     * @param inventory the inventory to manage slots form
     */
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

    /**
     * A prompt for how to sort an inventory
     * @param inventory the inventory to sort
     */
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

    /**
     * A prompt for managing a slot
     * @param inventory the inventory in which the slot exists
     * @param slotIndex the index of the slot to manage
     * @throws InvalidInputException thrown when an invalid slot index is used
     */
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

    /**
     * A prompt to show all available items and then insert into the given slot index
     * @param inventory the inventory in which to insert an item
     * @param slotIndex the slot index in which to insert an item
     */
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

    /**
     * A prompt for swapping two slots.
     * It doesn't matter which slot is passed first, and which is selected.
     * @param inventory the inventory in which to swap items
     * @param slot1 the first slot selected
     */
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

    /**
     * Wrapper for the static function with the same name to not pass in the scanner every time.
     */
    private int readOption(int min, int max) throws InvalidInputException {
        return readOption(scanner, min, max);
    }

    /**
     * Read a number input in a range.
     * If the number is not in this range, invalidInputText is printed, and you
     * get to choose again
     * @param min minimum option value (inclusive)
     * @param max maximum option value (inclusive)
     * @return the selected option
     * @throws InvalidInputException when an invalid input is given
     */
    public static int readOption(Scanner scanner, int min, int max) throws InvalidInputException {
        return readOption(scanner, IntStream.rangeClosed(min, max).toArray());
    }

    /**
     * Read a value that exists in the allowed array
     * @param scanner which scanner to read input from.
     * @param allowed which values are allowed.
     * @return the valid selected option.
     * @throws InvalidInputException if the selected value is invalid.
     */
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

