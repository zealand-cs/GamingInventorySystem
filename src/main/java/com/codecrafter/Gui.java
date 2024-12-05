package com.codecrafter;

import com.codecrafter.database.InventorySystemRepository;
import com.codecrafter.exceptions.InvalidSlotException;
import com.codecrafter.exceptions.TooMuchWeightException;
import com.codecrafter.inventory.*;
import com.codecrafter.items.ItemManager;

import java.util.Scanner;

public class Gui {
    private final InventorySystemRepository repository;
    Scanner scanner = new Scanner(System.in);

    Gui(InventorySystemRepository repository) {
        this.repository = repository;
    }

    void start() {
        while (true) {
            var inventory = selectInventory();
            inventoryManagement(inventory);
        }
    }

    Inventory selectInventory() {
        var inventories = repository.getInventories();

        while (true) {
            System.out.println("[0] Select inventory");
            System.out.println("[1] New inventory");
            System.out.println("[2] Import inventory");

            var selectedOption = scanner.nextInt();
            scanner.nextLine();

            switch (selectedOption) {
                case 0 -> {
                    if (inventories.isEmpty()) {
                        System.out.println("No inventories to select");
                    } else {

                        for (int i = 0; i < (long) inventories.size(); i++) {
                            System.out.println("[" + i + "] " + inventories.get(i).getName());
                        }

                        var inventory = scanner.nextInt();
                        scanner.nextLine();
                        return inventories.get(inventory);
                    }
                }
                case 1 -> { return createInventory(); }
                case 2 -> {
                    // TODO: Import an inventory file
                    System.out.println("Not implemented");
                }
            }

        }
    }

    Inventory createInventory() {
        System.out.println("Name of the new inventory: ");

        var name = scanner.nextLine();

        return repository.newInventory(name, 32);
    }

    void inventoryManagement(Inventory inventory) {
        System.out.println("Showing inventory " + inventory.getName());

        inventoryLoop: while(true) {
            System.out.println("[0] Save and leave inventory");
            System.out.println("[1] Show stats");
            System.out.println("[2] Show slots");
            System.out.println("[3] Export inventory");
            System.out.println("[9] Delete inventory");

            var selection = scanner.nextInt();
            scanner.nextLine();

            switch (selection) {
                case 0 -> {
                    repository.deleteInventory(inventory.getId());
                    repository.saveInventory(inventory);
                    break inventoryLoop;
                }
                case 1 -> {
                    System.out.println("Name: " + inventory.getName());
                    System.out.println("Weight: " + inventory.getWeight());
                    System.out.println("Unlocked slots: " + inventory.getUnlockedSlots());
                }
                case 2 -> manageSlots(inventory);
                case 3 -> {
                    // TODO: Implement export to file
                    System.out.println("Not implemented");
                }
                case 9 -> {
                    repository.deleteInventory(inventory.getId());
                    System.out.println("Deleted inventory");
                    break inventoryLoop;
                }
            }
        }
    }

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

            int field = scanner.nextInt();
            scanner.nextLine();

            if (field >= 0 && field < inventory.getUnlockedSlots()) {
                try {
                    manageSlot(inventory, field);
                } catch (Exception e) {
                    System.out.println("Invalid slot");
                }
            } else if (field == inventory.getUnlockedSlots()) {
                break;
            } else if (field == inventory.getUnlockedSlots() + 1) {
                sortSlots(inventory);
            }
        }
    }


    void sortSlots(Inventory inventory) {
        System.out.println("[0] Back");
        System.out.println("[1] Sort by id");
        System.out.println("[2] Sort by name");
        System.out.println("[3] Sort by item type");
        System.out.println("[4] Sort by weight");

        var input = scanner.nextInt();
        scanner.nextLine();

        // Handle input. If zero, none are hit and the function returns anyway.
        switch (input) {
            case 1 -> inventory.sortInventory(SortValue.Id);
            case 2 -> inventory.sortInventory(SortValue.Alphabetical);
            case 3 -> inventory.sortInventory(SortValue.ItemType);
            case 4 -> inventory.sortInventory(SortValue.Weight);
        }
    }


    void manageSlot(Inventory inventory, int slotIndex) throws InvalidSlotException {
        manageSlotLoop: while (true) {
            Slot slot = inventory.getSlot(slotIndex);

            if (slot.isEmpty()) {
                System.out.println("Slot is empty");
                System.out.println("[0] Back");
                System.out.println("[1] Insert item");

                var option = scanner.nextInt();
                scanner.nextLine();

                if (option == 0) {
                    break;
                } else if (option == 1) {
                    var items = ItemManager.getInstance().getItems();
                    System.out.println("Select item to insert");

                    for (int i = 0; i < items.size(); i++) {
                        Item item = items.get(i);
                        System.out.println("[" + i + "] " + item.getName());
                    }

                    var itemSelect = scanner.nextInt();
                    scanner.nextLine();

                    Item item = items.get(itemSelect);
                    slot.setItem(item);
                    slot.setCount(1);
                }
            } else {
                System.out.println("Item: " + slot.getItem().getName() + "(id: " + slot.getItem().getId() + ")");
                System.out.println("Stack size: " + slot.getCount());
                System.out.println("Weight: " + slot.getWeight());
                System.out.println();

                System.out.println("[0] Back");
                System.out.println("[1] Use item");
                System.out.println("[2] Increment");
                System.out.println("[3] Decrement");
                System.out.println("[4] Remove item");
                System.out.println("[5] Swap item");

                var option = scanner.nextInt();
                scanner.nextLine();

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
            }
        }
    }

    void swapSlots(Inventory inventory, int slot1) {
        System.out.println("Select slot to swap with");

        var latestOption = showInventory(inventory);

        System.out.println("[" + (latestOption + 1) + "] Back");

        var input = scanner.nextInt();
        scanner.nextLine();

        if (input >= 0 && input < inventory.getUnlockedSlots()) {
            inventory.swapSlots(slot1, input);
            System.out.println("Swapped slots");
        }
    }
}
