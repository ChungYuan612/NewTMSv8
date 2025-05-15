package me.cyperion.ntms.Bazaar;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Main API: register items, place/cancel orders, get book.
 */
public class TradingAPI {
    private final NewTMSv8 plugin;
    private final Map<String, MarketItem> items = new LinkedHashMap<>();
    private final Map<String, OrderBook> books = new HashMap<>();

    public TradingAPI(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public void registerItem(String id, ItemStack stack) {
        MarketItem mi = new MarketItem(id, stack);
        items.put(id, mi);
        books.put(id, new OrderBook(plugin, mi));
    }

    public Order placeOrder(UUID player, String itemId, Order.Type type, int qty, double price) {
        MarketItem mi = items.get(itemId);
        Order o = new Order(type, mi, player, qty, price);
        books.get(itemId).addOrder(o);
        return o;
    }

    public void cancelOrder(String itemId, Order order) {
        books.get(itemId).cancelOrder(order);
    }

    public OrderBook getOrderBook(String itemId) {
        return books.get(itemId);
    }
}