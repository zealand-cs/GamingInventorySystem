package com.codecrafter.database;

import com.codecrafter.inventory.Inventory;
import com.codecrafter.inventory.Item;
import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DatabaseRepository implements InventorySystemRepository {
    public static final int DATABASE_VERSION = 1;

    Connection connection;

    public DatabaseRepository(String file) {
        try {
            DriverManager.registerDriver(new org.sqlite.JDBC());

            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);

            connection = DriverManager.getConnection("jdbc:sqlite:" + file, config.toProperties());
            connection.setAutoCommit(false);

            connection.commit();
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
                            "id INTEGER PRIMARY KEY," +
                            "inventory INTEGER REFERENCES inventories(id) ON DELETE CASCADE," +
                            "count INTEGER," +
                            "position INTEGER" +
                    ")"
            );
            statement.addBatch(
                    "CREATE TABLE slot_items (" +
                            "id INTEGER PRIMARY KEY," +
                            "slot INTEGER REFERENCES inventory_slots(id) ON DELETE CASCADE," +
                            "itemType VARCHAR(128)," +
                            "name VARCHAR(256)," +
                            "weight REAL," +
                            "maxStack INTEGER" +
                    ")"
            );
        }
        statement.addBatch("PRAGMA user_version = " + DATABASE_VERSION);
        statement.executeBatch();

        connection.commit();

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

            connection.commit();

            return inventories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Inventory newInventory(String name, int unlockedSlots) {
        try {
            var inventoryStatement = connection.prepareStatement("INSERT INTO inventories (name, unlockedSlots) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            inventoryStatement.setString(1, name);
            inventoryStatement.setInt(2, unlockedSlots);

            inventoryStatement.executeUpdate();
            connection.commit();

            var generatedKeys = inventoryStatement.getGeneratedKeys();
            generatedKeys.next();

            return new Inventory(generatedKeys.getInt(1), name, unlockedSlots);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveInventory(Inventory inventory) {
        try (
                var inventoryStatement = connection.prepareStatement("INSERT INTO inventories (name, unlockedSlots) VALUES (?, ?)");
                var slotStatement = connection.prepareStatement("INSERT INTO inventory_slots (inventory, count, position) VALUES (?, ?, ?)");
                var slotItemStatement = connection.prepareStatement("INSERT INTO slot_items (slot, itemType, name, weight, maxStack) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
             ) {

            inventoryStatement.setString(1, inventory.getName());
            inventoryStatement.setInt(2, inventory.getUnlockedSlots());
            inventoryStatement.executeUpdate();
            connection.commit();

            var slots = inventory.getSlots();
            for (int i = 0; i < slots.length; i++) {
                var slot = slots[i];
                if (slot.isNotEmpty()) {
                    var item = slot.getItem();

                    slotStatement.setInt(1, inventory.getId());
                    slotStatement.setInt(2, slot.getCount());
                    slotStatement.setInt(3, i);
                    slotStatement.executeUpdate();

                    var generatedKeys = slotStatement.getGeneratedKeys();
                    generatedKeys.next();

                    slotItemStatement.setInt(1, generatedKeys.getInt(1));
                    slotItemStatement.setString(2, item.getType().name());
                    slotItemStatement.setString(3, item.getName());
                    slotItemStatement.setDouble(4, item.getWeight());
                    slotItemStatement.setInt(5, item.getMaxStack());
                    slotItemStatement.executeUpdate();

                    connection.commit();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteInventory(int inventoryId) {
        try {
            var inventoryStatement = connection.prepareStatement("DELETE FROM inventories WHERE id = ?");
            inventoryStatement.setInt(1, inventoryId);
            inventoryStatement.execute();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Item> getItems() {
        return List.of();
    }
}
