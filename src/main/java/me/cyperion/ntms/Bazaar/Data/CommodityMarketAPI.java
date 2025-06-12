package me.cyperion.ntms.Bazaar.Data;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * 商品交易市場API - 模擬股票市場機制用於Minecraft商品交易
 */
public class CommodityMarketAPI {

    private final SQLDatabase sqlDatabase;

    public CommodityMarketAPI(SQLDatabase sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
        createTable();
    }

    /**
     * 創建訂單表
     */
    private void createTable() {
        String autoIncrementSyntax = sqlDatabase.getType() == SQLDatabase.Type.SQLITE ? "AUTOINCREMENT" : "AUTO_INCREMENT";

        try (PreparedStatement statement = sqlDatabase.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS orders (" +
                "id INTEGER PRIMARY KEY " + autoIncrementSyntax + ", " +
                "product_id VARCHAR(32) NOT NULL, " +
                "amount INTEGER NOT NULL, " +
                "unit_price DOUBLE NOT NULL, " +
                "order_type VARCHAR(16) NOT NULL, " +
                "player VARCHAR(36) NOT NULL, " +
                "filled INTEGER DEFAULT 0, " +
                "claimed INTEGER DEFAULT 0, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 訂單類型枚舉
     */
    public enum OrderType {
        BUY("BUY"),
        SELL("SELL");

        private final String value;

        OrderType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 訂單實體類
     */
    public static class Order {
        private int id;
        private String productId;
        private int amount;
        private double unitPrice;
        private OrderType orderType;
        private String player;
        private int filled;
        private int claimed;
        private LocalDateTime createdAt;

        // 構造函數和getter/setter
        public Order(int id, String productId, int amount, double unitPrice,
                     OrderType orderType, String player, int filled, int claimed, LocalDateTime createdAt) {
            this.id = id;
            this.productId = productId;
            this.amount = amount;
            this.unitPrice = unitPrice;
            this.orderType = orderType;
            this.player = player;
            this.filled = filled;
            this.claimed = claimed;
            this.createdAt = createdAt;
        }

        // Getters
        public int getId() { return id; }
        public String getProductId() { return productId; }
        public int getAmount() { return amount; }
        public double getUnitPrice() { return unitPrice; }
        public OrderType getOrderType() { return orderType; }
        public String getPlayer() { return player; }
        public int getFilled() { return filled; }
        public int getClaimed() { return claimed; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public int getRemaining() { return amount - filled; }
        public boolean isFullyFilled() { return filled >= amount; }
        public double getTotalValue() { return amount * unitPrice; }
        public double getFilledValue() { return filled * unitPrice; }
    }

    /**
     * 交易結果類
     */
    public static class TradeResult {
        private final boolean success;
        private final String message;
        private final List<Integer> filledOrderIds;
        private final int totalFilled;
        private final double totalValue;

        public TradeResult(boolean success, String message, List<Integer> filledOrderIds,
                           int totalFilled, double totalValue) {
            this.success = success;
            this.message = message;
            this.filledOrderIds = filledOrderIds;
            this.totalFilled = totalFilled;
            this.totalValue = totalValue;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<Integer> getFilledOrderIds() { return filledOrderIds; }
        public int getTotalFilled() { return totalFilled; }
        public double getTotalValue() { return totalValue; }
    }

    /**
     * 市場數據類
     */
    public static class MarketData {
        private final String productId;
        private final double highestBuyPrice;
        private final double lowestSellPrice;
        private final double lastTradePrice;
        private final int totalBuyVolume;
        private final int totalSellVolume;

        public MarketData(String productId, double highestBuyPrice, double lowestSellPrice,
                          double lastTradePrice, int totalBuyVolume, int totalSellVolume) {
            this.productId = productId;
            this.highestBuyPrice = highestBuyPrice;
            this.lowestSellPrice = lowestSellPrice;
            this.lastTradePrice = lastTradePrice;
            this.totalBuyVolume = totalBuyVolume;
            this.totalSellVolume = totalSellVolume;
        }

        public String getProductId() { return productId; }
        public double getHighestBuyPrice() { return highestBuyPrice; }
        public double getLowestSellPrice() { return lowestSellPrice; }
        public double getLastTradePrice() { return lastTradePrice; }
        public int getTotalBuyVolume() { return totalBuyVolume; }
        public int getTotalSellVolume() { return totalSellVolume; }
        public double getSpread() { return lowestSellPrice - highestBuyPrice; }
    }

    /**
     * 下買單
     */
    public TradeResult placeBuyOrder(String productId, int amount, double unitPrice, String player) {
        return placeOrder(productId, amount, unitPrice, OrderType.BUY, player);
    }

    /**
     * 下賣單
     */
    public TradeResult placeSellOrder(String productId, int amount, double unitPrice, String player) {
        return placeOrder(productId, amount, unitPrice, OrderType.SELL, player);
    }

    /**
     * 下訂單的核心邏輯
     */
    private TradeResult placeOrder(String productId, int amount, double unitPrice, OrderType orderType, String player) {
        if (amount <= 0) {
            return new TradeResult(false, "訂單數量必須大於0", new ArrayList<>(), 0, 0.0);
        }
        if (unitPrice <= 0) {
            return new TradeResult(false, "訂單價格必須大於0", new ArrayList<>(), 0, 0.0);
        }

        try {
            sqlDatabase.getConnection().setAutoCommit(false);

            // 先嘗試匹配現有訂單
            TradeResult matchResult = matchOrders(productId, amount, unitPrice, orderType, player);

            // 如果還有剩餘數量，創建新訂單
            int remainingAmount = amount - matchResult.getTotalFilled();
            if (remainingAmount > 0) {
                createOrder(productId, remainingAmount, unitPrice, orderType, player);
            }

            sqlDatabase.getConnection().commit();

            String message = matchResult.getTotalFilled() > 0 ?
                    String.format("成功匹配 %d 件商品，剩餘 %d 件掛單", matchResult.getTotalFilled(), remainingAmount) :
                    String.format("已掛單 %d 件商品", remainingAmount);

            return new TradeResult(true, message, matchResult.getFilledOrderIds(),
                    matchResult.getTotalFilled(), matchResult.getTotalValue());

        } catch (SQLException e) {
            try {
                sqlDatabase.getConnection().rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return new TradeResult(false, "數據庫錯誤：" + e.getMessage(), new ArrayList<>(), 0, 0.0);
        } finally {
            try {
                sqlDatabase.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 匹配訂單
     */
    private TradeResult matchOrders(String productId, int amount, double unitPrice, OrderType orderType, String player) throws SQLException {
        List<Integer> filledOrderIds = new ArrayList<>();
        int totalFilled = 0;
        double totalValue = 0.0;

        // 查找可匹配的訂單
        String oppositeOrderType = orderType == OrderType.BUY ? "SELL" : "BUY";
        String priceCondition = orderType == OrderType.BUY ? "<= ?" : ">= ?";
        String orderBy = orderType == OrderType.BUY ? "ORDER BY unit_price ASC, created_at ASC" : "ORDER BY unit_price DESC, created_at ASC";

        String query = "SELECT * FROM orders WHERE product_id = ? AND order_type = ? AND unit_price " +
                priceCondition + " AND (amount - filled) > 0 " + orderBy;

        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(query)) {
            stmt.setString(1, productId);
            stmt.setString(2, oppositeOrderType);
            stmt.setDouble(3, unitPrice);
            //stmt.setString(4, player);

            ResultSet rs = stmt.executeQuery();

            while (rs.next() && totalFilled < amount) {
                int orderId = rs.getInt("id");
                int orderAmount = rs.getInt("amount");
                int orderFilled = rs.getInt("filled");
                double orderPrice = rs.getDouble("unit_price");

                int availableAmount = orderAmount - orderFilled;
                int fillAmount = Math.min(availableAmount, amount - totalFilled);

                // 更新匹配的訂單
                updateOrderFilled(orderId, orderFilled + fillAmount);

                filledOrderIds.add(orderId);
                totalFilled += fillAmount;
                totalValue += fillAmount * orderPrice;
            }
        }

        return new TradeResult(true, "match successful", filledOrderIds, totalFilled, totalValue);
    }

    /**
     * 創建新訂單
     */
    private void createOrder(String productId, int amount, double unitPrice, OrderType orderType, String player) throws SQLException {
        String sql = "INSERT INTO orders (product_id, amount, unit_price, order_type, player) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setInt(2, amount);
            stmt.setDouble(3, unitPrice);
            stmt.setString(4, orderType.getValue());
            stmt.setString(5, player);
            stmt.executeUpdate();
        }
    }

    /**
     * 更新訂單填充數量
     */
    private void updateOrderFilled(int orderId, int newFilled) throws SQLException {
        String sql = "UPDATE orders SET filled = ? WHERE id = ?";
        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, newFilled);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

    /**
     * 領取訂單(ClaimOrder的數量是總共已經領多少)
     */
    public void claimOrder(int orderId, int claimAmount){
        String sql = "UPDATE orders SET claimed = ? WHERE id = ?";
        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, claimAmount);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消訂單
     */
    public boolean cancelOrder(int orderId, String player) {
        try {
            String sql = "DELETE FROM orders WHERE id = ? AND player = ? AND filled = 0";
            try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
                stmt.setInt(1, orderId);
                stmt.setString(2, player);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 獲取玩家的訂單
     */
    public List<Order> getPlayerOrders(String player) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE player = ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
            stmt.setString(1, player);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(createOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * 獲取特定商品的市場數據
     */
    public MarketData getMarketData(String productId) {
        double highestBuyPrice = 0.0;
        double lowestSellPrice = Double.MAX_VALUE;
        double lastTradePrice = 0.0;
        int totalBuyVolume = 0;
        int totalSellVolume = 0;

        try {
            // 獲取最高買價
            String buyQuery = "SELECT MAX(unit_price) as max_price, SUM(amount - filled) as volume FROM orders " +
                    "WHERE product_id = ? AND order_type = 'BUY' AND (amount - filled) > 0";
            try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(buyQuery)) {
                stmt.setString(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    highestBuyPrice = rs.getDouble("max_price");
                    totalBuyVolume = rs.getInt("volume");
                }
            }

            // 獲取最低賣價
            String sellQuery = "SELECT MIN(unit_price) as min_price, SUM(amount - filled) as volume FROM orders " +
                    "WHERE product_id = ? AND order_type = 'SELL' AND (amount - filled) > 0";
            try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sellQuery)) {
                stmt.setString(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    lowestSellPrice = rs.getDouble("min_price");
                    totalSellVolume = rs.getInt("volume");
                    if (lowestSellPrice == 0) lowestSellPrice = Double.MAX_VALUE;
                }
            }

            // 獲取最近交易價格（通過已填充的訂單推算）
            String lastPriceQuery = "SELECT unit_price FROM orders WHERE product_id = ? AND filled > 0 " +
                    "ORDER BY created_at DESC LIMIT 1";
            try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(lastPriceQuery)) {
                stmt.setString(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    lastTradePrice = rs.getDouble("unit_price");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lowestSellPrice == Double.MAX_VALUE) {
            lowestSellPrice = 0.0;
        }

        return new MarketData(productId, highestBuyPrice, lowestSellPrice,
                lastTradePrice, totalBuyVolume, totalSellVolume);
    }

    /**
     * 獲取訂單簿（買賣掛單列表）
     * @return Map<String, List<Order>> key為買賣掛單類型,value為掛單列表
     */
    public Map<String, List<Order>> getOrderBook(String productId, int limit) {
        Map<String, List<Order>> orderBook = new HashMap<>();

        // 獲取買單（按價格從高到低）
        List<Order> buyOrders = getOrdersByType(productId, OrderType.BUY, "ORDER BY unit_price DESC", limit);
        orderBook.put("BUY", buyOrders);

        // 獲取賣單（按價格從低到高）
        List<Order> sellOrders = getOrdersByType(productId, OrderType.SELL, "ORDER BY unit_price ASC", limit);
        orderBook.put("SELL", sellOrders);

        return orderBook;
    }

    /**
     * 獲取特定類型的訂單
     */
    private List<Order> getOrdersByType(String productId, OrderType orderType, String orderBy, int limit) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE product_id = ? AND order_type = ? AND (amount - filled) > 0 " +
                orderBy + " LIMIT ?";

        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
            stmt.setString(1, productId);
            stmt.setString(2, orderType.getValue());
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(createOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * 從ResultSet創建Order對象
     */
    private Order createOrderFromResultSet(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("id"),
                rs.getString("product_id"),
                rs.getInt("amount"),
                rs.getDouble("unit_price"),
                OrderType.valueOf(rs.getString("order_type")),
                rs.getString("player"),
                rs.getInt("filled"),
                rs.getInt("claimed"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    /**
     * 獲取熱門商品列表
     */
    public List<String> getPopularProducts(int limit) {
        List<String> products = new ArrayList<>();
        String sql = "SELECT product_id, COUNT(*) as order_count FROM orders " +
                "WHERE created_at > datetime('now', '-7 days') " +
                "GROUP BY product_id ORDER BY order_count DESC LIMIT ?";

        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(rs.getString("product_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    /**
     * 清理已完成的舊訂單
     */
    public int cleanupOldOrders(int daysOld) {
        String sql = "DELETE FROM orders WHERE filled = amount AND created_at < datetime('now', '-' || ? || ' days')";
        try (PreparedStatement stmt = sqlDatabase.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, daysOld);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}

