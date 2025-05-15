package me.cyperion.ntms.Bazaar.Event;

import me.cyperion.ntms.Bazaar.Order;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OrderEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Order order;

    public OrderEvent(Order order) {
        this.order = order;
    }
    public Order getOrder() { return order; }
    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}

