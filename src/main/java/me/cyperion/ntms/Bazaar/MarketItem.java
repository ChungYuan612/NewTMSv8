package me.cyperion.ntms.Bazaar;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Represents a tradable item. Users can register new items.
 */
public class MarketItem {
    private final String id;
    private final ItemStack itemStack;

    public MarketItem(String id, ItemStack stack) {
        this.id = id;
        this.itemStack = stack.clone();
    }
    public String getId() { return id; }
    public ItemStack getItemStack() { return itemStack.clone(); }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarketItem)) return false;
        return id.equals(((MarketItem) o).id);
    }
    @Override public int hashCode() { return Objects.hash(id); }
}