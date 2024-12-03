package com.codecrafter.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    static Connection connection;

    public static void initialize(String file) {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + file)) {
            connection = con;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static Connection getInstance() throws DatabaseManagerNotInitializedException {
        if (connection == null) {
            throw new DatabaseManagerNotInitializedException();
        }

        return connection;
    }

    public void migrate() throws DatabaseManagerNotInitializedException {
        Connection con = getInstance();
    }
}

