package me.cyperion.ntms.Menu.BazaarMenu;

import me.cyperion.ntms.Bazaar.Data.BazaarItem;
import me.cyperion.ntms.Bazaar.Data.CommodityMarketAPI;
import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

// ============================================================================
// BazaarOrderBookMenu.java - 訂單簿介面 (顯示買賣掛單)
// ============================================================================
public class BazaarOrderBookMenu extends Menu {

    private final BazaarItem bazaarItem;
    private final CommodityMarketAPI tradingAPI;
    private ItemStack background;
    private ItemStack backButton;
    private ItemStack itemDisplay;
    private ItemStack refreshButton;

    public BazaarOrderBookMenu(PlayerMenuUtility utility, NewTMSv8 plugin,
                               BazaarItem bazaarItem, CommodityMarketAPI tradingAPI) {
        super(utility, plugin);
        this.bazaarItem = bazaarItem;
        this.tradingAPI = tradingAPI;
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&3訂單簿 - " + bazaarItem.getIcon().getItemMeta().getDisplayName());
    }

    @Override
    public int getSolts() {
        return 6 * 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if (!event.isLeftClick()) return;
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 45: // 返回按鈕
                new BazaarItemDetailMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI).open();
                break;

            case 53: // 刷新按鈕
                setMenuItems(); // 重新載入訂單簿
                player.sendMessage(colors("&a訂單簿已刷新！"));
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // 清空庫存
        inventory.clear();

        // 填充背景
        for (int i = 0; i < getSolts(); i++) {
            inventory.setItem(i, background);
        }

        // 設置固定物品
        inventory.setItem(4, itemDisplay);
        inventory.setItem(45, backButton);
        inventory.setItem(53, refreshButton);

        // 獲取訂單簿數據
        try {

            Map<String, List<CommodityMarketAPI.Order>> orderBook = tradingAPI.getOrderBook(bazaarItem.getProductId(), 32678);

            List<CommodityMarketAPI.Order> buyOrders = orderBook.get("BUY");
            List<CommodityMarketAPI.Order> sellOrders = orderBook.get("SELL");

            // 顯示買單標題
            ItemStack buyTitle = new ItemStack(Material.EMERALD);
            ItemMeta buyTitleMeta = buyTitle.getItemMeta();
            buyTitleMeta.setDisplayName(colors("&a買單 (由高到低)"));
            List<String> buyTitleLore = new ArrayList<>();
            buyTitleLore.add(colors("&7玩家願意購買的價格"));
            buyTitleLore.add(colors("&7總共 " + buyOrders.size() + " 筆買單"));
            buyTitleMeta.setLore(buyTitleLore);
            buyTitle.setItemMeta(buyTitleMeta);
            inventory.setItem(9, buyTitle);

            // 顯示賣單標題
            ItemStack sellTitle = new ItemStack(Material.REDSTONE);
            ItemMeta sellTitleMeta = sellTitle.getItemMeta();
            sellTitleMeta.setDisplayName(colors("&c賣單 (由低到高)"));
            List<String> sellTitleLore = new ArrayList<>();
            sellTitleLore.add(colors("&7玩家願意出售的價格"));
            sellTitleLore.add(colors("&7總共 " + sellOrders.size() + " 筆賣單"));
            sellTitleMeta.setLore(sellTitleLore);
            sellTitle.setItemMeta(sellTitleMeta);
            inventory.setItem(17, sellTitle);

            // 顯示買單 (左側，槽位 18-26, 27-35)
            int buyStartSlot = 18;
            for (int i = 0; i < Math.min(buyOrders.size(), 16); i++) {
                CommodityMarketAPI.Order order = buyOrders.get(i);
                ItemStack orderItem = createOrderDisplay(order, true);

                int slot;
                if (i < 8) {
                    slot = buyStartSlot + i;
                } else {
                    slot = buyStartSlot + 9 + (i - 8);
                }
                inventory.setItem(slot, orderItem);
            }

            // 顯示賣單 (右側，槽位 19-26, 28-35)
            int sellStartSlot = 19;
            for (int i = 0; i < Math.min(sellOrders.size(), 16); i++) {
                CommodityMarketAPI.Order order = sellOrders.get(i);
                ItemStack orderItem = createOrderDisplay(order, false);

                int slot;
                if (i < 7) {
                    slot = sellStartSlot + i;
                } else {
                    slot = sellStartSlot + 9 + (i - 7);
                }
                inventory.setItem(slot, orderItem);
            }

        } catch (Exception e) {
            // 如果獲取訂單失敗，顯示錯誤信息
            ItemStack errorItem = new ItemStack(Material.BARRIER);
            ItemMeta errorMeta = errorItem.getItemMeta();
            errorMeta.setDisplayName(colors("&c載入訂單失敗"));
            List<String> errorLore = new ArrayList<>();
            errorLore.add(colors("&7無法獲取訂單簿數據"));
            errorLore.add(colors("&7請稍後再試"));
            errorMeta.setLore(errorLore);
            errorItem.setItemMeta(errorMeta);
            inventory.setItem(22, errorItem);

            e.printStackTrace();
        }
    }

    private ItemStack createOrderDisplay(CommodityMarketAPI.Order order, boolean isBuyOrder) {
        ItemStack display = new ItemStack(isBuyOrder ? Material.GREEN_WOOL : Material.RED_WOOL);
        ItemMeta meta = display.getItemMeta();

        meta.setDisplayName(colors((isBuyOrder ? "&a買單" : "&c賣單") + " #" + order.getId()));

        List<String> lore = new ArrayList<>();
        lore.add(colors(""));
        lore.add(colors("&7價格：&6" + String.format("%.2f", order.getUnitPrice()) + " 元"));
        lore.add(colors("&7數量：&b" + order.getRemaining()));
        lore.add(colors("&7總價：&6" + String.format("%.2f", order.getUnitPrice() * order.getRemaining()) + " 元"));
        lore.add(colors(""));

        // 如果有玩家信息，顯示玩家名稱（可選）
        if (order.getPlayer() != null && !order.getPlayer().isEmpty()) {
            //你可以通過UUID獲取玩家名稱
            Player orderPlayer = Bukkit.getPlayer(UUID.fromString(order.getPlayer()));
            if (orderPlayer != null) {
                lore.add(colors("&7下單者：&e" + orderPlayer.getName()));
            } else {
            lore.add(colors("&7下單者：&e" + order.getPlayer().substring(0, 8) + "..."));
            }
        }

        // 顯示下單時間（如果Order類有時間戳）

        long timeAgo = System.currentTimeMillis() - order.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
        String timeStr = formatTimeAgo(timeAgo);
        lore.add(colors("&7下單時間：&7" + timeStr + "前"));


        meta.setLore(lore);
        display.setItemMeta(meta);
        return display;
    }

    private String formatTimeAgo(long millisAgo) {
        long seconds = millisAgo / 1000;
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分鐘";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "小時";
        } else {
            return (seconds / 86400) + "天";
        }
    }

    private void init() {
        // 背景
        background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // 返回按鈕
        backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(colors("&e返回"));
        List<String> backLore = new ArrayList<>();
        backLore.add(colors("&7點擊返回商品詳細頁面"));
        backMeta.setLore(backLore);
        backButton.setItemMeta(backMeta);

        // 商品展示
        itemDisplay = bazaarItem.getIcon().clone();
        ItemMeta itemMeta = itemDisplay.getItemMeta();
        List<String> itemLore = new ArrayList<>();
        itemLore.add(colors(""));
        itemLore.add(colors("&6=== 訂單簿 ==="));
        itemLore.add(colors("&7顯示當前所有買賣掛單"));
        itemLore.add(colors("&a綠色 = 買單 (玩家要購買)"));
        itemLore.add(colors("&c紅色 = 賣單 (玩家要出售)"));
        itemMeta.setLore(itemLore);
        itemDisplay.setItemMeta(itemMeta);

        // 刷新按鈕
        refreshButton = new ItemStack(Material.CLOCK);
        ItemMeta refreshMeta = refreshButton.getItemMeta();
        refreshMeta.setDisplayName(colors("&b刷新訂單簿"));
        List<String> refreshLore = new ArrayList<>();
        refreshLore.add(colors("&7點擊重新載入最新訂單"));
        refreshMeta.setLore(refreshLore);
        refreshButton.setItemMeta(refreshMeta);
    }
}