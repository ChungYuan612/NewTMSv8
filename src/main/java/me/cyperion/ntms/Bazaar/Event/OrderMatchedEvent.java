package me.cyperion.ntms.Bazaar.Event;

import me.cyperion.ntms.Bazaar.Order;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OrderMatchedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Order buyOrder, sellOrder;
    private final int quantity;
    private final double price;
    public OrderMatchedEvent(Order buy, Order sell, int qty, double price) {
        this.buyOrder = buy; this.sellOrder = sell;
        this.quantity = qty; this.price = price;
    }
    public Order getBuyOrder() { return buyOrder; }
    public Order getSellOrder() { return sellOrder; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}