package me.cyperion.ntms.Menu.BazaarMenu;

import me.cyperion.ntms.Bazaar.Data.BazaarItem;
import me.cyperion.ntms.Bazaar.Data.CommodityMarketAPI;
import me.cyperion.ntms.ItemStacks.Item.Emerald_Coins;
import me.cyperion.ntms.ItemStacks.Item.JadeCore;
import me.cyperion.ntms.ItemStacks.Item.Materaial.ReinfinedLapis;
import me.cyperion.ntms.ItemStacks.NTMSItems;
import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

public class BazaarMenu extends Menu {

    private ItemStack background;
    private ItemStack close;
    private List<BazaarItem> bazaarItems;
    private final CommodityMarketAPI commodityMarketAPI;

    public BazaarMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
        commodityMarketAPI = plugin.getCommodityMarketAPI();
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&3市場");
    }

    @Override
    public int getSolts() {
        return 4*9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(!event.isLeftClick()) return;
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        // 關閉按鈕
        if(slot == 31) {
            player.closeInventory();
            return;
        }

        // 檢查是否點擊商品
        int itemIndex = mapToCenterIndex(slot);
        if(itemIndex != -1 && itemIndex < bazaarItems.size()) {
            BazaarItem selectedItem = bazaarItems.get(itemIndex);
            // 打開商品詳細介面
            new BazaarItemDetailMenu(this.playerMenuUtility, plugin, selectedItem, commodityMarketAPI).open();
        }
    }

    @Override
    public void setMenuItems() {
        for(int i = 0; i < 4*9; i++){
            if(i == 31) {
                inventory.setItem(i, close);
            } else if(mapToCenterIndex(i) == -1) {
                inventory.setItem(i, background);
            } else if(mapToCenterIndex(i) < bazaarItems.size()) {
                BazaarItem item = bazaarItems.get(mapToCenterIndex(i));
                inventory.setItem(i, toDisplay(item));
            }
        }
    }

    private ItemStack toDisplay(BazaarItem bazaarItem) {
        ItemStack item = bazaarItem.getIcon().clone();
        try {
            ItemMeta meta = item.getItemMeta();
            ArrayList<String> lore = new ArrayList<>();
            // 獲取市場數據
            CommodityMarketAPI.MarketData marketData = commodityMarketAPI.getMarketData(bazaarItem.getProductId());

            lore.add(colors(""));

            // 顯示價格信息
            String buyPriceString = marketData.getLowestSellPrice() > 0 ?
                    String.format("%.2f", marketData.getLowestSellPrice()) : "N/A";
            String sellPriceString = marketData.getHighestBuyPrice() > 0 ?
                    String.format("%.2f", marketData.getHighestBuyPrice()) : "N/A";

            lore.add(colors("&a最低賣出價格：&f" + buyPriceString + " &6元"));
            lore.add(colors("&c最高買入價格：&f" + sellPriceString + " &6元"));

            // 顯示交易量
            if(marketData.getTotalSellVolume() > 0 || marketData.getTotalBuyVolume() > 0) {
                lore.add(colors(""));
                lore.add(colors("&7賣單量：&e" + marketData.getTotalSellVolume()));
                lore.add(colors("&7買單量：&e" + marketData.getTotalBuyVolume()));
            }

            // 顯示價差
            if(marketData.getSpread() > 0) {
                lore.add(colors("&7價差：&6" + String.format("%.2f", marketData.getSpread())));
            }

            lore.add(colors(""));
            lore.add(colors("&e點擊查看詳細交易介面"));

            meta.setLore(lore);
            item.setItemMeta(meta);

        } catch (Exception e) {
            plugin.getLogger().warning("[Bazaar] 在顯示展示的物品時出現錯誤: " + e.getMessage());
        }

        return item;
    }

    private void init() {
        // 背景
        background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        List<String> clickLore = new ArrayList<>();
        clickLore.add("");
        clickLore.add(colors("&e左鍵點擊"));

        // 關閉按鈕
        close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(colors("&c關閉"));
        closeMeta.setLore(clickLore);
        close.setItemMeta(closeMeta);

        // 初始化商品列表
        bazaarItems = new ArrayList<>();
        bazaarItems.add(new BazaarItem(
                NTMSItems.REINFINED_LAPIS.getBazaarID(),
                new ReinfinedLapis(plugin).toItemStack()
        ));
        bazaarItems.add(new BazaarItem(
                NTMSItems.EMERALD_COINS.getBazaarID(),
                new Emerald_Coins().toItemStack()
        ));
        bazaarItems.add(new BazaarItem(
                NTMSItems.JADE_CORE.getBazaarID(),
                new JadeCore().toItemStack()
        ));
    }

    /**
     * 將 slot index（0~35）轉為中間 7x2 區域中的編號（0~13）
     * 若不在區域內，回傳 -1
     */
    private static int mapToCenterIndex(int slotIndex) {
        int row = slotIndex / 9;   // 第幾排（0～3）
        int col = slotIndex % 9;   // 第幾欄（0～8）

        // 中間區域只包含 row 1 和 2，col 1～7（排除邊緣）
        if ((row == 1 || row == 2) && (col >= 1 && col <= 7)) {
            int rowOffset = (row - 1) * 7;         // 第 2 排起始是 +0，第 3 排起始是 +7
            return rowOffset + (col - 1);          // col 1 對應 offset 0
        }

        return -1; // 不在中心區域
    }
}



