package me.cyperion.ntms.Bazaar;

import me.cyperion.ntms.Bazaar.Event.OrderCancelledEvent;
import me.cyperion.ntms.Bazaar.Event.OrderMatchedEvent;
import me.cyperion.ntms.Bazaar.Event.OrderPlacedEvent;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.*;


/**
 * Manages the order book for a specific item.
 */
public class OrderBook {
    private final MarketItem item;
    private final PriorityQueue<Order> buys = new PriorityQueue<>(Comparator
            .<Order>comparingDouble(Order::getPrice).reversed()
            .thenComparingLong(Order::getTimestamp));
    private final PriorityQueue<Order> sells = new PriorityQueue<>(Comparator
            .comparingDouble(Order::getPrice)
            .thenComparingLong(Order::getTimestamp));
    private final NewTMSv8 plugin;

    public OrderBook(NewTMSv8 plugin, MarketItem item) {
        this.plugin = plugin;
        this.item = item;
        loadFromDB();
    }

    public void addOrder(Order order) {
        if (order.getType() == Order.Type.BUY) buys.offer(order);
        else sells.offer(order);
        persistOrder(order);
        Bukkit.getPluginManager().callEvent(new OrderPlacedEvent(order));
        match();
    }

    public void cancelOrder(Order order) {
        if (order.getType() == Order.Type.BUY) buys.remove(order);
        else sells.remove(order);
        removeOrderFromDB(order.getId());
        Bukkit.getPluginManager().callEvent(new OrderCancelledEvent(order));
    }

    private void match() {
        while (!buys.isEmpty() && !sells.isEmpty()) {
            Order b = buys.peek(), s = sells.peek();
            if (b.getPrice() >= s.getPrice()) {
                int qty = Math.min(b.getQuantity(), s.getQuantity());
                double price = (b.getPrice() + s.getPrice())/2;
                Bukkit.getPluginManager().callEvent(new OrderMatchedEvent(b,s,qty,price));
                adjust(b, qty); adjust(s, qty);
            } else break;
        }
    }
    private void adjust(Order o, int qty) {
        if (o.getQuantity() > qty) {
            o.setQuantity(o.getQuantity()-qty);
            persistOrder(o);
        } else {
            if (o.getType()==Order.Type.BUY) buys.poll(); else sells.poll();
            removeOrderFromDB(o.getId());
        }
    }


    private void persistOrder(Order order) {
        try (Connection conn = DBManager.getConnection()) {
            String sql;
            if (order.getId() < 0) {
                // Insert new order
                sql = "INSERT INTO orders(type,item,player,quantity,price,timestamp) VALUES(?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, order.getType().name());
                    ps.setString(2, order.getItem().getId());
                    ps.setString(3, order.getPlayer().toString());
                    ps.setInt(4, order.getQuantity());
                    ps.setDouble(5, order.getPrice());
                    ps.setLong(6, order.getTimestamp());
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        long newId = rs.getLong(1);
                        // note: order.id is final; in practice use a mutable field or return new Order
                    }
                }
            } else {
                // Update existing
                sql = "UPDATE orders SET quantity=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, order.getQuantity());
                    ps.setLong(2, order.getId());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeOrderFromDB(long orderId) {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM orders WHERE id=?")) {
            ps.setLong(1, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadFromDB() {
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM orders WHERE item=?")) {
            ps.setString(1, item.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                        rs.getLong("id"),
                        Order.Type.valueOf(rs.getString("type")),
                        item,
                        UUID.fromString(rs.getString("player")),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getLong("timestamp")
                );
                if (order.getType() == Order.Type.BUY) buys.offer(order);
                else sells.offer(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}