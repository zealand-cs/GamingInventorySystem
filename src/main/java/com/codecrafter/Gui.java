package com.codecrafter;

import com.codecrafter.database.InventorySystemRepository;
import com.codecrafter.exceptions.InvalidSlotException;
import com.codecrafter.exceptions.TooMuchWeightException;
import com.codecrafter.inventory.*;
import com.codecrafter.items.ItemManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Gui {
    private final InventorySystemRepository repository;
    Scanner scanner = new Scanner(System.in);

    Gui(InventorySystemRepository repository) {
        this.repository = repository;
    }

    void start() {
        mainLoop: while (true) {
            System.out.println("[1] Select inventory");
            System.out.println("[2] New inventory");
            System.out.println("[3] Import inventory");
            System.out.println("[0] Exit");

            var inventories = repository.getInventories();

            try {
                var selectedOption = readOption(0, 3);
                Inventory inventory = null;
                switch (selectedOption) {
                    case 1 -> inventory = selectInventory(inventories);
                    case 2 -> inventory = createInventory();
                    case 3 -> {
                        // TODO: Import an inventory file
                        System.out.println("Not implemented");
                    }
                    default -> {
                        break mainLoop;
                    }
                };

                if (inventory != null) {
                    inventoryManagement(inventory);
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid option");
            }
        }
    }

    private Inventory selectInventory(List<Inventory> inventories) {
        while (true) {
            int offset = 1;
            for (int i = 0; i < (long) inventories.size(); i++) {
                System.out.println("[" + (i + offset) + "] " + inventories.get(i).getName());
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

    Inventory createInventory() {
        System.out.print("Name of the new inventory: ");

        var name = scanner.nextLine();

        return repository.newInventory(name, 32);
    }

    void inventoryManagement(Inventory inventory) {
        System.out.println("Showing inventory " + inventory.getName());

        inventoryLoop: while(true) {
            System.out.println("[1] Show stats");
            System.out.println("[2] Show slots");
            System.out.println("[3] Export inventory");
            System.out.println("[4] Delete inventory");
            System.out.println("[0] Save and leave inventory");

            try {
                var option = readOption(0, 4);

                switch (option) {
                    case 0 -> {
                        repository.deleteInventory(inventory.getId());
                        repository.saveInventory(inventory);
                        break inventoryLoop;
                    }
                    case 1 -> {
                        System.out.println("Name: " + inventory.getName());
                        System.out.println("Weight: " + inventory.getWeight());
                        System.out.println("Unlocked slots: " + inventory.getUnlockedSlots());
                        System.out.println();
                    }
                    case 2 -> manageSlots(inventory);
                    case 3 -> {
                        // TODO: Implement export to file
                        System.out.println("Not implemented");
                    }
                    case 4 -> {
                        repository.deleteInventory(inventory.getId());
                        System.out.println("Deleted inventory");
                        break inventoryLoop;
                    }
                }
            } catch (InvalidInputException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /// Prints all inventory slots in the terminal shown as options. Starting at selection index startIndex.
    ///
    /// Returns an int which is the latest index showed
    int showInventory(Inventory inventory) {
        int rowLength = 8;

        var slots = inventory.getSlots();

        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];

            if (i != 0 && i % rowLength == 0) {
                System.out.println("|");
            }

            if (slot.isNotEmpty()) {
                System.out.print("| [" + i + "] " + slot.getItem().getName() + " (" + slot.getCount() + ") ");
            } else {
                System.out.print("| [" + i + "] empty ");
            }
        }

        System.out.println("|");

        return inventory.getUnlockedSlots() - 1;
    }

    void manageSlots(Inventory inventory) {
        while (true) {
            var latestOption = showInventory(inventory);

            System.out.println("[" + (latestOption + 1) + "] Back");
            System.out.println("[" + (latestOption + 2) + "] Sort");

            try {
                var option = readOption(0, latestOption + 2);

                if (option == inventory.getUnlockedSlots()) {
                    break;
                } else if (option == inventory.getUnlockedSlots() + 1) {
                    sortSlots(inventory);
                } else {
                    manageSlot(inventory, option);
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid slot or option selected");
            }
        }
    }


    void sortSlots(Inventory inventory) {
        while (true) {
            System.out.println("[1] Sort by id");
            System.out.println("[2] Sort by name");
            System.out.println("[3] Sort by item type");
            System.out.println("[4] Sort by weight");
            System.out.println("[0] Back");

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


    void manageSlot(Inventory inventory, int slotIndex) throws InvalidInputException {
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
                        case 1 -> insertItemToSlot(slot);
                    }
                } catch (InvalidInputException e) {
                    System.out.println("Invalid input");
                }
            } else {
                System.out.println("Item: " + slot.getItem().getName() + "(id: " + slot.getItem().getId() + ")");
                System.out.println("Stack size: " + slot.getCount());
                System.out.println("Weight: " + slot.getWeight());
                System.out.println();

                System.out.println("[1] Use item");
                System.out.println("[2] Increment");
                System.out.println("[3] Decrement");
                System.out.println("[4] Remove item");
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
                                System.out.println("Too much weight to add more items to inventory");
                            }
                        }
                        case 3 -> inventory.decrementSlot(slotIndex);
                        case 4 -> inventory.clearSlot(slotIndex);
                        case 5 -> { swapSlots(inventory, slotIndex); break manageSlotLoop; }
                    }
                } catch (InvalidInputException e) {
                    System.out.println("Invalid input");
                }
            }
        }
    }

    private void insertItemToSlot(Slot slot) {
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
                slot.setItem(item);
                slot.setCount(1);

                break;
            } catch (InvalidInputException e) {
                System.out.println("Invalid item selected");
            }
        }
    }

    void swapSlots(Inventory inventory, int slot1) {
        while (true) {
            System.out.println("Select slot to swap with");

            var latest = showInventory(inventory);
            System.out.println("[" + (latest + 1) + "] Back");

            try {
                var inputOption = readOption(0, latest + 1);

                if (inputOption != latest + 1) {
                    inventory.swapSlots(slot1, inputOption);
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
    int readOption(int min, int max) throws InvalidInputException {
        return readOption(IntStream.rangeClosed(min, max).toArray());
    }

    /// Read a number input in an array that's allowed
    int readOption(int[] allowed) throws InvalidInputException {
        var option = scanner.nextInt();
        scanner.nextLine();

        if (IntStream.of(allowed).anyMatch(x -> x == option)) {
            return option;
        }
        throw new InvalidInputException();
    }
}

class InvalidInputException extends IOException {

}
