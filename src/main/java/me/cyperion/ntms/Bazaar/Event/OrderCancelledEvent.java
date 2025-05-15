package me.cyperion.ntms.Bazaar.Event;

import me.cyperion.ntms.Bazaar.Order;

public class OrderCancelledEvent extends OrderEvent {
    public OrderCancelledEvent(Order order) {
        super(order);
    }
}