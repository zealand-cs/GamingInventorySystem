package com.codecrafter;

import com.codecrafter.database.DatabaseRepository;
import com.codecrafter.database.InventorySystemRepository;

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

        InventorySystemRepository repo = dbRepo;

        var gui = new Gui(repo);
        gui.start();
    }
}

