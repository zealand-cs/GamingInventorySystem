package com.codecrafter;

import com.codecrafter.database.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        String dbFile = "gis.db";
        DatabaseManager.initialize(dbFile);

        System.out.println("Loading inventory database");

        Gui.start();
    }
}

