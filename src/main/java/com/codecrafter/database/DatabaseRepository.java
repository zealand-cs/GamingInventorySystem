package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;

import java.sql.*;
import java.util.List;


public class DatabaseRepository implements InventoryRepository {
    public static final int DATABASE_VERSION = 1;

    Connection connection;

    public DatabaseRepository(String file) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void migrate() throws SQLException {
        System.out.println("Migrating database");

        var versionStatement = connection.createStatement();
        var res = versionStatement.executeQuery("PRAGMA user_version");

        int currentVersion;
        if (res.next()) {
            currentVersion = res.getInt(1);
        } else {
            currentVersion = 0;
        }

        if (currentVersion == DATABASE_VERSION) {
            System.out.println("Database already up to date");
            return;
        }

        Statement statement = connection.createStatement();

        if (currentVersion < 1) {
            statement.addBatch(
                        "CREATE TABLE inventory (" +
                            "id integer PRIMARY KEY" +
                        ")"
            );
            statement.addBatch(
                        "CREATE TABLE items (" +
                            "id integer PRIMARY KEY" +
                        ")"
            );
            statement.addBatch(
                        "CREATE TABLE item_attributes (" +
                            "id integer PRIMARY KEY" +
                        ")"
            );
        }
        statement.addBatch("PRAGMA user_version = " + DATABASE_VERSION);
        statement.executeBatch();

        System.out.println("Migrated database to newest version: " + DATABASE_VERSION);
    }

    @Override
    public List<Inventory> getInventories() {
        return List.of();
    }

    @Override
    public Inventory getInventory(int id) {
        return null;
    }

    @Override
    public void addItemToInventory(int inventoryId, Item item) {

    }

    @Override
    public void removeInventorySlot(int inventoryId, int slotId) {

    }

    @Override
    public void getInventorySlots() {

    }
}

