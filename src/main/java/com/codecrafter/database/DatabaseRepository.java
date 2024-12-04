package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseRepository implements InventorySystemRepository {
    public static final int DATABASE_VERSION = 1;

    Connection connection;

    public DatabaseRepository(String file) {
        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());
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
                    "CREATE TABLE inventories (" +
                            "id integer PRIMARY KEY," +
                            "name varchar(128)," +
                            "unlockedSlots integer" +
                    ")"
            );
            statement.addBatch(
                    "CREATE TABLE inventory_slots (" +
                            "id integer PRIMARY KEY," +
                            "inventory integer REFERENCES inventory(id)," +
                            "count integer" +
                            "position integer" +
                    ")"
            );
            statement.addBatch(
                    "CREATE TABLE inventory_items (" +
                            "id integer PRIMARY KEY," +
                            "slot integer REFERENCES inventory_slots(id)," +
                            "itemType varchar(128)," +
                            "name varchar(256)," +
                            "weight real," +
                            "maxStack integer" +
                    ")"
            );
            statement.addBatch(
                    "CREATE TABLE item_attributes (" +
                            "id integer PRIMARY KEY," +
                            "name varchar(64)," +
                            "value varchar(128)" +
                    ")"
            );
        }
        statement.addBatch("PRAGMA user_version = " + DATABASE_VERSION);
        statement.executeBatch();

        System.out.println("Migrated database to newest version: " + DATABASE_VERSION);
    }

    @Override
    public List<Inventory> getInventories() {
        try {
            List<Inventory> inventories = new ArrayList<>();
            var statement = connection.createStatement();
            var inventoryResult = statement.executeQuery("SELECT id, name, unlockedSlots FROM inventories");

            while (inventoryResult.next()) {
                Inventory inventory = new Inventory(inventoryResult.getInt("id"), inventoryResult.getString("name"), inventoryResult.getInt("unlockedSlots"));
                inventories.add(inventory);
            }

            return inventories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveInventory(Inventory inventory) {
        try {
            var statement = connection.prepareStatement("INSERT INTO inventories (name, unlockedSlots) VALUES (?, ?)");
            statement.setString(1, inventory.getName());
            statement.setInt(2, inventory.getUnlockedSlots());
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteInventory(int inventoryId) {
        try {
            var statement = connection.prepareStatement("DELETE FROM inventories WHERE id = ?");
            statement.setInt(1, inventoryId);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}