package me.cyperion.ntms.Bazaar.Event;

import me.cyperion.ntms.Bazaar.Order;

public class OrderPlacedEvent extends OrderEvent {
    public OrderPlacedEvent(Order order) {
        super(order);
    }
}