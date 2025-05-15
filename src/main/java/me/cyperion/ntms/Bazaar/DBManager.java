package me.cyperion.ntms.Bazaar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connection and operations.
 */
class DBManager {
    private static final String URL = "jdbc:sqlite:NewTMSv8/SupplyDemand.db";

    static {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS orders ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "type TEXT NOT NULL,"
                    + "item TEXT NOT NULL,"
                    + "player TEXT NOT NULL,"
                    + "quantity INTEGER NOT NULL,"
                    + "price REAL NOT NULL,"
                    + "timestamp INTEGER NOT NULL)"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}