package me.cyperion.ntms.Bazaar.Data;

import java.sql.*;
import java.io.File;
import java.util.logging.Logger;

/**
 * SQLiteDatabase實作類別
 * 實作SQLDatabase接口，提供SQLite數據庫連接和管理功能
 */
public class SQLiteDatabase implements SQLDatabase {

    private final String databasePath;
    private final Logger logger;
    private Connection connection;
    private boolean isConnected = false;

    /**
     * 構造函數
     * @param databasePath 數據庫文件路徑
     * @param logger 日誌記錄器
     */
    public SQLiteDatabase(String databasePath, Logger logger) {
        this.databasePath = databasePath;
        this.logger = logger;
    }

    /**
     * 構造函數（使用默認路徑）
     * @param pluginDataFolder 插件數據文件夾
     * @param logger 日誌記錄器
     */
    public SQLiteDatabase(File pluginDataFolder, Logger logger) {
        this(new File(pluginDataFolder, "market.db").getAbsolutePath(), logger);
    }

    /**
     * 初始化數據庫連接
     * @return 是否成功連接
     */
    public boolean initialize() {
        try {
            // 確保數據庫文件的父目錄存在
            File dbFile = new File(databasePath);
            File parentDir = dbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    logger.severe("無法創建數據庫目錄: " + parentDir.getAbsolutePath());
                    return false;
                }
            }

            // 加載SQLite JDBC驅動
            Class.forName("org.sqlite.JDBC");

            // 創建連接
            String url = "jdbc:sqlite:" + databasePath;
            connection = DriverManager.getConnection(url);

            // 設置連接屬性
            setupConnection();

            isConnected = true;
            logger.info("成功連接到SQLite數據庫: " + databasePath);
            return true;

        } catch (ClassNotFoundException e) {
            logger.severe("SQLite JDBC驅動未找到: " + e.getMessage());
            return false;
        } catch (SQLException e) {
            logger.severe("數據庫連接失敗: " + e.getMessage());
            return false;
        }
    }

    /**
     * 設置連接屬性
     */
    private void setupConnection() throws SQLException {
        if (connection == null) return;

        // 啟用外鍵約束
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        // 設置WAL模式以提高並發性能
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA journal_mode = WAL");
        }

        // 設置同步模式
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA synchronous = NORMAL");
        }

        // 設置緩存大小（以頁為單位，每頁通常1024字節）
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA cache_size = 10000");
        }

        // 設置臨時存儲模式
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA temp_store = MEMORY");
        }
    }

    /**
     * 獲取數據庫連接
     * @return Connection對象
     */
    @Override
    public Connection getConnection() {
        if (!isConnected || connection == null) {
            throw new RuntimeException("數據庫未連接，請先調用initialize()方法");
        }

        // 檢查連接是否仍然有效
        try {
            if (connection.isClosed()) {
                logger.warning("數據庫連接已關閉，嘗試重新連接...");
                if (!initialize()) {
                    throw new RuntimeException("重新連接數據庫失敗");
                }
            }
        } catch (SQLException e) {
            logger.severe("檢查數據庫連接狀態時發生錯誤: " + e.getMessage());
            throw new RuntimeException("數據庫連接錯誤", e);
        }

        return connection;
    }

    /**
     * 獲取數據庫類型
     * @return SQLDatabase.Type.SQLITE
     */
    @Override
    public Type getType() {
        return Type.SQLITE;
    }

    /**
     * 檢查是否已連接
     * @return 連接狀態
     */
    public boolean isConnected() {
        return isConnected && connection != null;
    }

    /**
     * 關閉數據庫連接
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                isConnected = false;
                logger.info("數據庫連接已關閉");
            } catch (SQLException e) {
                logger.severe("關閉數據庫連接時發生錯誤: " + e.getMessage());
            }
        }
    }

    /**
     * 執行數據庫備份
     * @param backupPath 備份文件路徑
     * @return 是否備份成功
     */
    public boolean backup(String backupPath) {
        if (!isConnected()) {
            logger.warning("數據庫未連接，無法執行備份");
            return false;
        }

        try {
            // 創建備份目錄
            File backupFile = new File(backupPath);
            File backupDir = backupFile.getParentFile();
            if (backupDir != null && !backupDir.exists()) {
                if (!backupDir.mkdirs()) {
                    logger.severe("無法創建備份目錄: " + backupDir.getAbsolutePath());
                    return false;
                }
            }

            // 執行備份SQL命令
            String backupSql = "backup to '" + backupPath + "'";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(backupSql);
            }

            logger.info("數據庫備份成功: " + backupPath);
            return true;

        } catch (SQLException e) {
            logger.severe("數據庫備份失敗: " + e.getMessage());
            return false;
        }
    }

    /**
     * 執行數據庫優化
     * @return 是否優化成功
     */
    public boolean optimize() {
        if (!isConnected()) {
            logger.warning("數據庫未連接，無法執行優化");
            return false;
        }

        try {
            // 執行VACUUM命令清理數據庫
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("VACUUM");
            }

            // 重新分析統計信息
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("ANALYZE");
            }

            logger.info("數據庫優化完成");
            return true;

        } catch (SQLException e) {
            logger.severe("數據庫優化失敗: " + e.getMessage());
            return false;
        }
    }

    /**
     * 檢查表是否存在
     * @param tableName 表名
     * @return 是否存在
     */
    public boolean tableExists(String tableName) {
        if (!isConnected()) {
            return false;
        }

        try {
            String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, tableName);
                try (ResultSet rs = stmt.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            logger.severe("檢查表存在性時發生錯誤: " + e.getMessage());
            return false;
        }
    }

    /**
     * 獲取數據庫文件大小（以字節為單位）
     * @return 文件大小，失敗返回-1
     */
    public long getDatabaseSize() {
        File dbFile = new File(databasePath);
        if (dbFile.exists()) {
            return dbFile.length();
        }
        return -1;
    }

    /**
     * 獲取數據庫統計信息
     * @return 統計信息字符串
     */
    public String getDatabaseStats() {
        if (!isConnected()) {
            return "數據庫未連接";
        }

        StringBuilder stats = new StringBuilder();
        stats.append("數據庫路徑: ").append(databasePath).append("\n");
        stats.append("文件大小: ").append(getDatabaseSize()).append(" 字節\n");

        try {
            // 獲取表數量
            String tableCountSql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(tableCountSql)) {
                if (rs.next()) {
                    stats.append("表數量: ").append(rs.getInt(1)).append("\n");
                }
            }

            // 獲取頁面信息
            String pageInfoSql = "PRAGMA page_count";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(pageInfoSql)) {
                if (rs.next()) {
                    stats.append("頁面數量: ").append(rs.getInt(1)).append("\n");
                }
            }

        } catch (SQLException e) {
            stats.append("獲取統計信息時發生錯誤: ").append(e.getMessage());
        }

        return stats.toString();
    }

    /**
     * 測試數據庫連接
     * @return 是否連接正常
     */
    public boolean testConnection() {
        if (!isConnected()) {
            return false;
        }

        try {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            logger.severe("測試數據庫連接時發生錯誤: " + e.getMessage());
            return false;
        }
    }

    /**
     * 獲取數據庫路徑
     * @return 數據庫文件路徑
     */
    public String getDatabasePath() {
        return databasePath;
    }
}