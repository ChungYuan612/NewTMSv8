package me.cyperion.ntms.Menu.BazaarMenu;


import me.cyperion.ntms.Bazaar.Data.BazaarItem;
import me.cyperion.ntms.Bazaar.Data.CommodityMarketAPI;
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
// ============================================================================
// BazaarItemDetailMenu.java - 商品詳細交易介面
// ============================================================================
public class BazaarItemDetailMenu extends Menu {

    private final BazaarItem bazaarItem;
    private final CommodityMarketAPI tradingAPI;
    private ItemStack background;
    private ItemStack backButton;
    private ItemStack instantBuy;
    private ItemStack instantSell;
    private ItemStack placeBuyOrder;
    private ItemStack placeSellOrder;
    private ItemStack orderBook;
    private ItemStack playerOrders;

    public BazaarItemDetailMenu(PlayerMenuUtility utility, NewTMSv8 plugin,
                                BazaarItem bazaarItem, CommodityMarketAPI tradingAPI) {
        super(utility, plugin);
        this.bazaarItem = bazaarItem;
        this.tradingAPI = tradingAPI;
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&3" + bazaarItem.getIcon().getItemMeta().getDisplayName() + " &3- 交易");
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
                new BazaarMenu(this.playerMenuUtility, plugin).open();
                break;

            case 20: // 快速購買
                handleInstantBuy(player);
                break;

            case 24: // 快速出售
                handleInstantSell(player);
                break;

            case 29: // 下買單
                new BazaarOrderPlaceMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI, true).open();
                break;

            case 33: // 下賣單
                new BazaarOrderPlaceMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI, false).open();
                break;

            case 38: // 查看訂單簿
                new BazaarOrderBookMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI).open();
                break;

            case 42: // 查看我的訂單
                new BazaarPlayerOrdersMenu(this.playerMenuUtility, plugin, tradingAPI, player.getUniqueId().toString()).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // 填充背景
        for (int i = 0; i < getSolts(); i++) {
            inventory.setItem(i, background);
        }

        // 設置商品展示
        inventory.setItem(13, createItemDisplay());

        // 設置功能按鈕
        inventory.setItem(45, backButton);
        inventory.setItem(20, instantBuy);
        inventory.setItem(24, instantSell);
        inventory.setItem(29, placeBuyOrder);
        inventory.setItem(33, placeSellOrder);
        inventory.setItem(38, orderBook);
        inventory.setItem(42, playerOrders);
    }

    private void handleInstantBuy(Player player) {
        CommodityMarketAPI.MarketData marketData = tradingAPI.getMarketData(bazaarItem.getProductId());
        if (marketData.getLowestSellPrice() <= 0) {
            player.sendMessage(colors("&c目前沒有可購買的商品！"));
            return;
        }

        // 這裡可以打開數量選擇介面，或直接購買1個
        new BazaarQuantitySelectMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI, true, marketData.getLowestSellPrice()).open();
    }

    private void handleInstantSell(Player player) {
        CommodityMarketAPI.MarketData marketData = tradingAPI.getMarketData(bazaarItem.getProductId());
        if (marketData.getHighestBuyPrice() <= 0) {
            player.sendMessage(colors("&c目前沒有人要購買這個商品！"));
            return;
        }

        // 這裡可以打開數量選擇介面，或直接出售1個
        new BazaarQuantitySelectMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI, false, marketData.getHighestBuyPrice()).open();
    }

    private ItemStack createItemDisplay() {
        ItemStack display = bazaarItem.getIcon().clone();
        ItemMeta meta = display.getItemMeta();
        List<String> lore = new ArrayList<>();

        CommodityMarketAPI.MarketData marketData = tradingAPI.getMarketData(bazaarItem.getProductId());

        lore.add(colors(""));
        lore.add(colors("&6=== 市場信息 ==="));
        lore.add(colors("&7最低賣價：&a" + (marketData.getLowestSellPrice() > 0 ?
                String.format("%.2f", marketData.getLowestSellPrice()) + " 元" : "N/A")));
        lore.add(colors("&7最高買價：&c" + (marketData.getHighestBuyPrice() > 0 ?
                String.format("%.2f", marketData.getHighestBuyPrice()) + " 元" : "N/A")));
        lore.add(colors("&7賣單量：&e" + marketData.getTotalSellVolume()));
        lore.add(colors("&7買單量：&e" + marketData.getTotalBuyVolume()));

        if (marketData.getLastTradePrice() > 0) {
            lore.add(colors("&7最近成交價：&6" + String.format("%.2f", marketData.getLastTradePrice()) + " 金幣"));
        }

        meta.setLore(lore);
        display.setItemMeta(meta);
        return display;
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
        backMeta.setDisplayName(colors("&e返回市場"));
        List<String> backLore = new ArrayList<>();
        backLore.add(colors("&7點擊返回市場主頁"));
        backMeta.setLore(backLore);
        backButton.setItemMeta(backMeta);

        // 快速購買
        instantBuy = new ItemStack(Material.EMERALD);
        ItemMeta buyMeta = instantBuy.getItemMeta();
        buyMeta.setDisplayName(colors("&a快速購買"));
        List<String> buyLore = new ArrayList<>();
        buyLore.add(colors("&7以當前最低價格立即購買"));
        buyLore.add(colors("&e點擊選擇數量"));
        buyMeta.setLore(buyLore);
        instantBuy.setItemMeta(buyMeta);

        // 快速出售
        instantSell = new ItemStack(Material.REDSTONE);
        ItemMeta sellMeta = instantSell.getItemMeta();
        sellMeta.setDisplayName(colors("&c快速出售"));
        List<String> sellLore = new ArrayList<>();
        sellLore.add(colors("&7以當前最高價格立即出售"));
        sellLore.add(colors("&e點擊選擇數量"));
        sellMeta.setLore(sellLore);
        instantSell.setItemMeta(sellMeta);

        // 下買單
        placeBuyOrder = new ItemStack(Material.GOLD_INGOT);
        ItemMeta buyOrderMeta = placeBuyOrder.getItemMeta();
        buyOrderMeta.setDisplayName(colors("&6下買單"));
        List<String> buyOrderLore = new ArrayList<>();
        buyOrderLore.add(colors("&7設定價格和數量下買單"));
        buyOrderLore.add(colors("&7等待其他玩家出售"));
        buyOrderLore.add(colors("&e點擊設置訂單"));
        buyOrderMeta.setLore(buyOrderLore);
        placeBuyOrder.setItemMeta(buyOrderMeta);

        // 下賣單
        placeSellOrder = new ItemStack(Material.IRON_INGOT);
        ItemMeta sellOrderMeta = placeSellOrder.getItemMeta();
        sellOrderMeta.setDisplayName(colors("&7下賣單"));
        List<String> sellOrderLore = new ArrayList<>();
        sellOrderLore.add(colors("&7設定價格和數量下賣單"));
        sellOrderLore.add(colors("&7等待其他玩家購買"));
        sellOrderLore.add(colors("&e點擊設置訂單"));
        sellOrderMeta.setLore(sellOrderLore);
        placeSellOrder.setItemMeta(sellOrderMeta);

        // 訂單簿
        orderBook = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = orderBook.getItemMeta();
        bookMeta.setDisplayName(colors("&9訂單簿"));
        List<String> bookLore = new ArrayList<>();
        bookLore.add(colors("&7查看當前買賣掛單"));
        bookLore.add(colors("&e點擊查看"));
        bookMeta.setLore(bookLore);
        orderBook.setItemMeta(bookMeta);

        // 我的訂單
        playerOrders = new ItemStack(Material.PAPER);
        ItemMeta playerOrdersMeta = playerOrders.getItemMeta();
        playerOrdersMeta.setDisplayName(colors("&b我的訂單"));
        List<String> playerOrdersLore = new ArrayList<>();
        playerOrdersLore.add(colors("&7查看和管理我的訂單"));
        playerOrdersLore.add(colors("&e點擊查看"));
        playerOrdersMeta.setLore(playerOrdersLore);
        playerOrders.setItemMeta(playerOrdersMeta);
    }
}