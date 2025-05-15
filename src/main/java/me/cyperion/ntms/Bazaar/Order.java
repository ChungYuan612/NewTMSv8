package me.cyperion.ntms.Bazaar;

import java.util.*;

/**
 * Represents a buy or sell order in the market.
 */
public class Order {
    public enum Type { BUY, SELL }

    private final long id;
    private final Type type;
    private final MarketItem item;
    private final UUID player;
    private int quantity;
    private final double price;
    private final long timestamp;

    public Order(long id, Type type, MarketItem item, UUID player, int quantity, double price, long timestamp) {
        this.id = id;
        this.type = type;
        this.item = item;
        this.player = player;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Order(Type type, MarketItem item, UUID player, int quantity, double price) {
        this(-1, type, item, player, quantity, price, System.currentTimeMillis());
    }

    // Getters and setters
    public long getId() { return id; }
    public Type getType() { return type; }
    public MarketItem getItem() { return item; }
    public UUID getPlayer() { return player; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public long getTimestamp() { return timestamp; }
}








