package me.cyperion.ntms.Menu.Bazaar;

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
// BazaarOrderPlaceMenu.java - 下單介面 (買單/賣單)
// ============================================================================
public class BazaarOrderPlaceMenu extends Menu {

    private final BazaarItem bazaarItem;
    private final CommodityMarketAPI tradingAPI;
    private final boolean isBuyOrder; // true = 買單, false = 賣單
    private ItemStack background;
    private ItemStack backButton;
    private ItemStack confirmButton;
    private ItemStack itemDisplay;
    private ItemStack priceDisplay;
    private ItemStack quantityDisplay;
    private ItemStack increasePrice;
    private ItemStack decreasePrice;
    private ItemStack increaseMorePrice;
    private ItemStack decreaseMorePrice;
    private ItemStack increaseQuantity;
    private ItemStack decreaseQuantity;

    private double currentPrice = 1000.0;
    private int currentQuantity = 1;

    public BazaarOrderPlaceMenu(PlayerMenuUtility utility, NewTMSv8 plugin,
                                BazaarItem bazaarItem, CommodityMarketAPI tradingAPI,
                                boolean isBuyOrder) {
        super(utility, plugin);
        this.bazaarItem = bazaarItem;
        this.tradingAPI = tradingAPI;
        this.isBuyOrder = isBuyOrder;

        // 設置初始價格為市場參考價格
        CommodityMarketAPI.MarketData marketData = tradingAPI.getMarketData(bazaarItem.getProductId());
        if (isBuyOrder && marketData.getHighestBuyPrice() > 0) {
            this.currentPrice = marketData.getHighestBuyPrice();
        } else if (!isBuyOrder && marketData.getLowestSellPrice() > 0) {
            this.currentPrice = marketData.getLowestSellPrice();
        }

        init();
    }

    @Override
    public String getMenuName() {
        return colors("&3" + (isBuyOrder ? "下買單" : "下賣單") + " - " +
                bazaarItem.getIcon().getItemMeta().getDisplayName());
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

            case 31: // 確認下單
                handleConfirmOrder(player);
                break;

            case 19: // 價格減少
                if (currentPrice > 0.1) {
                    currentPrice = Math.round((currentPrice - 0.1) * 100.0) / 100.0;
                    updateDisplays();
                }
                break;

            case 21: // 價格增加0.1
                currentPrice = Math.round((currentPrice + 0.1) * 100.0) / 100.0;
                updateDisplays();
                break;
            case 28: // 價格減少 10
                if (currentPrice > 10.0) {
                    currentPrice = Math.round((currentPrice - 10.0) * 100.0) / 100.0;
                    updateDisplays();
                }
                break;
            case 30: // 價格增加 10
                if (currentPrice > 10.0) {
                    currentPrice = Math.round((currentPrice + 10.0) * 100.0) / 100.0;
                    updateDisplays();
                }
                break;

            case 23: // 數量減少
                if (currentQuantity > 1) {
                    currentQuantity--;
                    updateDisplays();
                }
                break;

            case 25: // 數量增加
                if (currentQuantity < 64) {
                    currentQuantity++;
                    updateDisplays();
                }
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // 填充背景
        for (int i = 0; i < getSolts(); i++) {
            inventory.setItem(i, background);
        }

        // 設置物品展示
        inventory.setItem(13, itemDisplay);

        // 設置控制按鈕
        inventory.setItem(45, backButton);
        inventory.setItem(31, confirmButton);

        // 價格控制
        inventory.setItem(19, decreasePrice);
        inventory.setItem(20, priceDisplay);
        inventory.setItem(21, increasePrice);

        inventory.setItem(28, decreaseMorePrice);
        inventory.setItem(30, increaseMorePrice);

        // 數量控制
        inventory.setItem(23, decreaseQuantity);
        inventory.setItem(24, quantityDisplay);
        inventory.setItem(25, increaseQuantity);

        //更新顯示
        updateDisplays();
    }

    private void handleConfirmOrder(Player player) {
        try {
            // 計算總價
            double totalCost = currentPrice * currentQuantity;

            if (isBuyOrder) {
                // 檢查玩家是否有足夠金錢
                // 這裡需要你的經濟系統API
                if (plugin.getEconomy().getBalance(player) < totalCost) {
                    player.sendMessage(colors("&c你沒有足夠的金錢！需要 " + String.format("%,.2f", totalCost) + " 元"));
                    return;
                }

                // 下買單
                CommodityMarketAPI.TradeResult result = tradingAPI.placeBuyOrder(
                        bazaarItem.getProductId(),
                        currentQuantity,
                        currentPrice,
                        player.getUniqueId().toString()
                );
                plugin.getLogger().fine(result.toString());

                if (result.isSuccess()) {
                    player.sendMessage(colors("&a成功下買單！價格：" + String.format("%.2f", currentPrice) +
                            " 元，數量：" + currentQuantity));
                    // 扣除玩家金錢
                    // economyAPI.withdrawBalance(player, totalCost);
                    plugin.getEconomy().withdrawPlayer(player, totalCost);
                } else {
                    player.sendMessage(colors("&c下買單失敗！"));
                }
            } else {
                // 檢查玩家是否有足夠物品
                ItemStack requiredItem = bazaarItem.getIcon().clone();
                requiredItem.setAmount(currentQuantity);

                if (!hasEnoughItems(player, requiredItem)) {
                    player.sendMessage(colors("&c你沒有足夠的物品！"));
                    return;
                }

                // 下賣單
                CommodityMarketAPI.TradeResult result = tradingAPI.placeSellOrder(
                        bazaarItem.getProductId(),
                        currentQuantity,
                        currentPrice,
                        player.getUniqueId().toString()
                );
                System.out.println(result.toString());

                if (result.isSuccess()) {
                    player.sendMessage(colors("&a成功下賣單！價格：" + String.format("%.2f", currentPrice) +
                            " 元，數量：" + currentQuantity));
                    // 移除玩家物品
                    removeItems(player, requiredItem);
                } else {
                    player.sendMessage(colors("&c下賣單失敗！"));
                }
            }

            // 返回商品詳細頁面
            new BazaarItemDetailMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI).open();

        } catch (Exception e) {
            player.sendMessage(colors("&c下單時發生錯誤！"));
            e.printStackTrace();
        }
    }

    private boolean hasEnoughItems(Player player, ItemStack required) {
        int totalAmount = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(required)) {
                totalAmount += item.getAmount();
            }
        }
        return totalAmount >= required.getAmount();
    }

    private void removeItems(Player player, ItemStack toRemove) {
        int remaining = toRemove.getAmount();
        for (int i = 0; i < player.getInventory().getSize() && remaining > 0; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.isSimilar(toRemove)) {
                int takeAmount = Math.min(remaining, item.getAmount());
                remaining -= takeAmount;

                if (takeAmount >= item.getAmount()) {
                    player.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - takeAmount);
                }
            }
        }
    }

    private void updateDisplays() {
        // 更新價格顯示
        ItemMeta priceMeta = priceDisplay.getItemMeta();
        List<String> priceLore = new ArrayList<>();
        priceLore.add(colors("&7當前價格：&6" + String.format("%.2f", currentPrice) + " 元"));
        priceLore.add(colors("&e點擊上方按鈕調整價格"));
        priceMeta.setLore(priceLore);
        priceDisplay.setItemMeta(priceMeta);

        // 更新數量顯示
        ItemMeta quantityMeta = quantityDisplay.getItemMeta();
        List<String> quantityLore = new ArrayList<>();
        quantityLore.add(colors("&7當前數量：&b" + currentQuantity));
        quantityLore.add(colors("&e點擊上方按鈕調整數量"));
        quantityMeta.setLore(quantityLore);
        quantityDisplay.setItemMeta(quantityMeta);

        // 更新確認按鈕
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(colors("&7總價：&6" + String.format("%,.2f", currentPrice * currentQuantity) + " 元"));
        confirmLore.add(colors("&7數量：&b" + currentQuantity));
        confirmLore.add(colors("&7單價：&6" + String.format("%,.2f", currentPrice) + " 元"));
        confirmLore.add(colors(""));
        confirmLore.add(colors("&e點擊確認" + (isBuyOrder ? "買單" : "賣單")));
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);

        // 更新庫存顯示
        inventory.setItem(20, priceDisplay);
        inventory.setItem(24, quantityDisplay);
        inventory.setItem(31, confirmButton);
    }

    private void init() {
        // 背景
        if(isBuyOrder)
            background = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        else
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

        // 物品展示
        itemDisplay = bazaarItem.getIcon().clone();

        // 價格控制按鈕
        decreasePrice = new ItemStack(Material.RED_CONCRETE);
        ItemMeta decreasePriceMeta = decreasePrice.getItemMeta();
        decreasePriceMeta.setDisplayName(colors("&c價格 -0.1"));
        decreasePrice.setItemMeta(decreasePriceMeta);

        increasePrice = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta increasePriceMeta = increasePrice.getItemMeta();
        increasePriceMeta.setDisplayName(colors("&a價格 +0.1"));
        increasePrice.setItemMeta(increasePriceMeta);

        decreaseMorePrice = new ItemStack(Material.RED_STAINED_GLASS);
        ItemMeta decreaseMorePriceMeta = decreaseMorePrice.getItemMeta();
        decreaseMorePriceMeta.setDisplayName(colors("&a價格 -10"));
        decreaseMorePrice.setItemMeta(decreaseMorePriceMeta);

        increaseMorePrice = new ItemStack(Material.GREEN_STAINED_GLASS);
        ItemMeta increaseMorePriceMeta = increaseMorePrice.getItemMeta();
        increaseMorePriceMeta.setDisplayName(colors("&a價格 +10"));
        increaseMorePrice.setItemMeta(increaseMorePriceMeta);



        // 數量控制按鈕
        decreaseQuantity = new ItemStack(Material.RED_CONCRETE);
        ItemMeta decreaseQuantityMeta = decreaseQuantity.getItemMeta();
        decreaseQuantityMeta.setDisplayName(colors("&c數量 -1"));
        decreaseQuantity.setItemMeta(decreaseQuantityMeta);

        increaseQuantity = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta increaseQuantityMeta = increaseQuantity.getItemMeta();
        increaseQuantityMeta.setDisplayName(colors("&a數量 +1"));
        increaseQuantity.setItemMeta(increaseQuantityMeta);

        // 價格顯示
        priceDisplay = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta priceMeta = priceDisplay.getItemMeta();
        priceMeta.setDisplayName(colors("&6價格設定"));
        priceDisplay.setItemMeta(priceMeta);

        // 數量顯示
        quantityDisplay = new ItemStack(Material.IRON_NUGGET);
        ItemMeta quantityMeta = quantityDisplay.getItemMeta();
        quantityMeta.setDisplayName(colors("&b數量設定"));
        quantityDisplay.setItemMeta(quantityMeta);

        // 確認按鈕
        confirmButton = new ItemStack(isBuyOrder ? Material.EMERALD_BLOCK : Material.GOLD_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(colors(isBuyOrder ? "&a確認下買單" : "&c確認下賣單"));
        confirmButton.setItemMeta(confirmMeta);

        // 初始化顯示
        //updateDisplays();
    }
}