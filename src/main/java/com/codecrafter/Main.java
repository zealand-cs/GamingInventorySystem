package com.codecrafter;

import com.codecrafter.database.FileRepository;
import com.codecrafter.database.InventorySystemRepository;

import com.codecrafter.exceptions.MalformedFileException;
import com.codecrafter.exceptions.InvalidInputException;
import com.codecrafter.inventory.Item;
import com.codecrafter.items.ConsumablePotion;
import com.codecrafter.items.ItemManager;
import com.codecrafter.items.WeaponItem;
import com.codecrafter.items.WeaponHandedness;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String dataFile = "inventories.json";

        FileRepository fileRepo = null;
        fileRepo = createFileRepository(fileRepo, dataFile);

        List<Item> items = new ArrayList<>() {{
            add(new WeaponItem(0, "Sword of Might", 0.5, 1, WeaponHandedness.MainHand, 10, 100));
            add(new WeaponItem(1, "Longsword", 1, 1, WeaponHandedness.TwoHand, 8, 150));
            add(new ConsumablePotion(2, "Healing Potion", 0.05, 20));
            add(new ConsumablePotion(3, "Heavy Stone", 2.5, 20));
        }};

        for (Item item : items) {
            ItemManager.getInstance().insertItem(item);
        }

        InventorySystemRepository repo = fileRepo;

        var gui = new Gui(repo);
        gui.start();
    }

    private static @NotNull FileRepository createFileRepository(FileRepository fileRepo, String dataFile) {
        while (fileRepo == null) {
            try {
                fileRepo = new FileRepository(dataFile);
            } catch (MalformedFileException e) {
                    try {
                        System.out.println("Data file is corrupted... These are your options");
                        System.out.println("[0] Delete file");
                        System.out.println("[1] Crash without deleting file (exit, with the hope of you fixing the mistake without loosing data)");
                        var scanner = new Scanner(System.in);
                        var option = Gui.readOption(scanner, 0, 1);

                        switch (option) {
                            case 0 -> {
                                var file = new File(dataFile);
                                if (file.delete()) {
                                    System.out.println("Deleted file and starting from scratch");
                                } else {
                                    System.out.println("Something went wrong when deleting. Crashing...");
                                    throw new RuntimeException("Crashed on purpose");
                                }
                            }
                            case 1 -> throw new RuntimeException("Crashed on purpose");
                        }
                    } catch (InvalidInputException ex) {
                        System.out.println("Invalid input.");
                    }
                }
        }
        return fileRepo;
    }
}

