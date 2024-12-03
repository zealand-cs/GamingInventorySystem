package com.codecrafter;

import com.codecrafter.database.DatabaseRepository;
import com.codecrafter.database.InventoryRepository;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String dbFile = "gis.db";
        DatabaseRepository dbRepo = new DatabaseRepository(dbFile);
        try {
            dbRepo.migrate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        InventoryRepository repo = dbRepo;

        Gui.start();
    }
}

