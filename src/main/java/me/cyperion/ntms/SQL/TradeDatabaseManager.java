package me.cyperion.ntms.SQL;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;

/**
 * 綠寶石貨幣交易介面
 */
public class TradeDatabaseManager {

    private final NewTMSv8 plugin;
    private Connection connection;

    public TradeDatabaseManager(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    /**
     * 取得 SQLite 連線，如果尚未建立則在 plugin 資料夾下建立 trade.db 檔案
     */
    public Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        File dbFile = new File(dataFolder, "trade.db");
        String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        connection = DriverManager.getConnection(url);
        return connection;
    }

    /**
     * 建立 orders 和 trades 兩個資料表（如果尚未存在）
     */
    public void createTables() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                    "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "player_id TEXT NOT NULL, " +
                    "order_type TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "order_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "status TEXT DEFAULT 'OPEN'" +
                    ");";
            stmt.executeUpdate(createOrdersTable);

            String createTradesTable = "CREATE TABLE IF NOT EXISTS trades (" +
                    "trade_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "buy_order_id INTEGER NOT NULL, " +
                    "sell_order_id INTEGER NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "trade_date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY(buy_order_id) REFERENCES orders(order_id), " +
                    "FOREIGN KEY(sell_order_id) REFERENCES orders(order_id)" +
                    ");";
            stmt.executeUpdate(createTradesTable);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //目前只有新增到DB而已
    public void addOrder(String playerId, String orderType, double price, int quantity) throws SQLException {
        String sql = "INSERT INTO orders (player_id, order_type, price, quantity) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerId);
            stmt.setString(2, orderType);
            stmt.setDouble(3, price);
            stmt.setInt(4, quantity);
            stmt.executeUpdate();
        }
    }

    //目前只有把訂單改為CANCELED

    /**
     *
     * @param orderId
     * @return 是否取消成功
     * @throws SQLException
     */
    public boolean cancelOrder(int orderId) throws SQLException {
        String sql = "UPDATE orders SET status = 'CANCELLED' WHERE order_id = ? AND status = 'OPEN'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("訂單 " + orderId + " 已取消。");
                return true;
            } else {
                System.out.println("訂單 " + orderId + " 無法取消（可能已完成或不存在）。");
                return false;
            }
        }
    }

}

