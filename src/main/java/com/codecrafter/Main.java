package com.codecrafter;

import com.codecrafter.database.FileRepository;
import com.codecrafter.database.InventorySystemRepository;

import com.codecrafter.inventory.Item;
import com.codecrafter.items.ConsumablePotion;
import com.codecrafter.items.ItemManager;
import com.codecrafter.items.WeaponItem;
import com.codecrafter.items.WeaponHandedness;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileFile = "inventories.json";
        FileRepository fileRepo = new FileRepository(fileFile);

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
}

