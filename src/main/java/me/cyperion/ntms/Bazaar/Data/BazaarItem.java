package me.cyperion.ntms.Bazaar.Data;



import org.bukkit.inventory.ItemStack;
// ============================================================================
// BazaarItem.java - 商品數據類別
// ============================================================================
public class BazaarItem {
    private final String productId;
    private final ItemStack icon;

    public BazaarItem(String productId, ItemStack icon) {
        this.productId = productId;
        this.icon = icon;
    }

    public String getProductId() {
        return productId;
    }

    public ItemStack getIcon() {
        return icon;
    }
}