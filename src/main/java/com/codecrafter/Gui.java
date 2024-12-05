package com.codecrafter;

import com.codecrafter.database.InventorySystemRepository;
import com.codecrafter.inventory.InvalidSlotException;
import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;
import com.codecrafter.inventory.Slot;
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

            var optionInput = scanner.nextInt();
            scanner.nextLine();

            if (optionInput == 0) {
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
            } else if (optionInput == 1) {
                return createInventory();
            } else if (optionInput == 2) {
                // TODO: Import an inventory file
                System.out.println("Not implemented");
            }
        }
    }

    Inventory createInventory() {
        System.out.println("Name of the new inventory: ");
        var name = scanner.nextLine();
        var inventory = new Inventory(0, name, 32);
        repository.saveInventory(inventory);
        return inventory;
    }

    void inventoryManagement(Inventory inventory) {
        System.out.println("Showing inventory " + inventory.getName());

        while(true) {
            System.out.println("[0] Save and leave inventory");
            System.out.println("[1] Show stats");
            System.out.println("[2] Show slots");
            System.out.println("[3] Export inventory");
            System.out.println("[9] Delete inventory");

            var selection = scanner.nextInt();
            scanner.nextLine();

            if (selection == 0) {
                break;
            } else if (selection == 1) {
                System.out.println("Name: " + inventory.getName());
                System.out.println("Weight: " + inventory.getWeight());
                System.out.println("Unlocked slots: " + inventory.getUnlockedSlots());
            } else if (selection == 2) {
                manageSlots(inventory);
            } else if (selection == 3) {
                // TODO: Implement export to file
                System.out.println("Not implemented");
            } else if (selection == 9) {
                repository.deleteInventory(inventory.getId());
                System.out.println("Deleted inventory");
                break;
            }
        }
    }

    void manageSlots(Inventory inventory) {
        while (true) {
            int rowLength = 8;

            // Print out rows of rowLength with slots in the inventory
            for (int row = 0; row < inventory.getUnlockedSlots() / rowLength; row++) {
                for (int column = 0; column < rowLength; column++) {
                    var index = (row * rowLength) + column;
                    try {
                        var slot = inventory.getSlot(index);

                        if (slot.isNotEmpty()) {
                            System.out.print("| [" + index + "] " + slot.getItem().getName() + " (" + slot.getCount() + ") ");
                        } else {
                            System.out.print("| [" + index + "] empty ");
                        }
                    } catch (InvalidSlotException e) {
                        System.out.println("Invalid slot used");
                    }
                }
                System.out.println("|");
            }

            System.out.println("[" + inventory.getUnlockedSlots() + "] Back");

            int field = scanner.nextInt();
            scanner.nextLine();

            if (field >= 0 || field < inventory.getUnlockedSlots()) {
                try {
                    manageSlot(inventory, field);
                } catch (Exception e) {
                    System.out.println("Invalid slot");
                }
            } else if (field == inventory.getUnlockedSlots() + 1) {
                break;
            }
        }
    }

    void manageSlot(Inventory inventory, int slotIndex) throws InvalidSlotException {
        while (true) {
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
                        System.out.println("[" + i + "] " + "(" + item.getId() + ") " + item.getName());
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

                var option = scanner.nextInt();
                scanner.nextLine();

                if (option == 0) {
                    break;
                } else if (option == 1) {
                    slot.use();
                } else if (option == 2) {
                    slot.setCount(slot.getCount() + 1);
                } else if (option == 3) {
                    slot.setCount(slot.getCount() - 1);
                } else if (option == 4) {
                    slot.clear();
                }
            }
        }
    }
}
