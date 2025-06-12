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
// BazaarQuantitySelectMenu.java - 數量選擇介面 (快速買賣)
// ============================================================================
public class BazaarQuantitySelectMenu extends Menu {

    private final BazaarItem bazaarItem;
    private final CommodityMarketAPI tradingAPI;
    private final boolean isBuying; // true = 購買, false = 出售
    private final double unitPrice; // 單價
    private ItemStack background;
    private ItemStack backButton;
    private ItemStack confirmButton;
    private ItemStack itemDisplay;
    private ItemStack quantityDisplay;
    private ItemStack increaseQuantity;
    private ItemStack decreaseQuantity;
    private ItemStack setQuantity1;
    private ItemStack setQuantity8;
    private ItemStack setQuantity16;
    private ItemStack setQuantity32;
    private ItemStack setQuantity64;

    private int currentQuantity = 1;

    public BazaarQuantitySelectMenu(PlayerMenuUtility utility, NewTMSv8 plugin,
                                    BazaarItem bazaarItem, CommodityMarketAPI tradingAPI,
                                    boolean isBuying, double unitPrice) {
        super(utility, plugin);
        this.bazaarItem = bazaarItem;
        this.tradingAPI = tradingAPI;
        this.isBuying = isBuying;
        this.unitPrice = unitPrice;
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&3" + (isBuying ? "購買" : "出售") + " - " +
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

            case 31: // 確認交易
                handleConfirmTrade(player);
                break;

            case 28: // 數量減少
                if (currentQuantity > 1) {
                    currentQuantity--;
                    updateDisplays();
                }
                break;

            case 34: // 數量增加
                if (currentQuantity < 64) {
                    currentQuantity++;
                    updateDisplays();
                }
                break;

            case 19: // 設置為1
                currentQuantity = 1;
                updateDisplays();
                break;

            case 20: // 設置為8
                currentQuantity = 8;
                updateDisplays();
                break;

            case 21: // 設置為16
                currentQuantity = 16;
                updateDisplays();
                break;

            case 22: // 設置為32
                currentQuantity = 32;
                updateDisplays();
                break;

            case 23: // 設置為64
                currentQuantity = 64;
                updateDisplays();
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

        // 數量控制
        inventory.setItem(28, decreaseQuantity);
        inventory.setItem(29, quantityDisplay);
        inventory.setItem(34, increaseQuantity);

        // 快速設置按鈕
        inventory.setItem(19, setQuantity1);
        inventory.setItem(20, setQuantity8);
        inventory.setItem(21, setQuantity16);
        inventory.setItem(22, setQuantity32);
        inventory.setItem(23, setQuantity64);

        updateDisplays();
    }

    private void handleConfirmTrade(Player player) {
        try {
            double totalCost = unitPrice * currentQuantity;

            if (isBuying) {
                // 執行購買
                //檢查玩家是否有足夠金錢
                if (plugin.getEconomy().getBalance(player) < totalCost) {
                    player.sendMessage(colors("&c你沒有足夠的金錢！需要 " + String.format("%.2f", totalCost) + " 元"));
                    return;
                }

                // 執行即時購買
                CommodityMarketAPI.TradeResult result = tradingAPI.placeBuyOrder(
                        bazaarItem.getProductId(),
                        currentQuantity,
                        unitPrice,
                        player.getUniqueId().toString()
                );

                if (result.isSuccess()) {
                    player.sendMessage(colors("&a成功購買 " + currentQuantity + " 個 " +
                            bazaarItem.getIcon().getItemMeta().getDisplayName() +
                            "！總花費：" + String.format("%.2f", totalCost) + " 元"));

                    // 扣除玩家金錢
                    // economyAPI.withdrawBalance(player, totalCost);
                    plugin.getEconomy().withdrawPlayer(player, totalCost);

                    // 給予玩家物品
                    ItemStack purchasedItem = bazaarItem.getIcon().clone();
                    purchasedItem.setAmount(currentQuantity);
                    giveItemToPlayer(player, purchasedItem);

                } else {
                    player.sendMessage(colors("&c購買失敗！可能沒有足夠的賣單。"));
                }

            } else {
                // 執行出售
                ItemStack requiredItem = bazaarItem.getIcon().clone();
                requiredItem.setAmount(currentQuantity);

                if (!hasEnoughItems(player, requiredItem)) {
                    player.sendMessage(colors("&c你沒有足夠的物品！"));
                    return;
                }

                // 執行即時出售
                CommodityMarketAPI.TradeResult result = tradingAPI.placeSellOrder(
                        bazaarItem.getProductId(),
                        currentQuantity,
                        unitPrice,
                        player.getUniqueId().toString()
                );

                if (result.isSuccess()) {
                    player.sendMessage(colors("&a成功出售 " + currentQuantity + " 個 " +
                            bazaarItem.getIcon().getItemMeta().getDisplayName() +
                            "！總收入：" + String.format("%.2f", totalCost) + " 元"));

                    // 移除玩家物品
                    removeItems(player, requiredItem);

                    // 給予玩家金錢
                    // economyAPI.depositBalance(player, totalCost);
                    plugin.getEconomy().depositPlayer(player, totalCost);

                } else {
                    player.sendMessage(colors("&c出售失敗！可能沒有足夠的買單。"));
                }
            }

            // 返回商品詳細頁面
            new BazaarItemDetailMenu(this.playerMenuUtility, plugin, bazaarItem, tradingAPI).open();

        } catch (Exception e) {
            player.sendMessage(colors("&c交易時發生錯誤！"));
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

    private void giveItemToPlayer(Player player, ItemStack item) {
        // 嘗試直接給予物品到背包
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(item);
        } else {
            // 背包滿了，掉落到地上
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(colors("&e背包已滿，物品已掉落到地上！"));
        }
    }

    private void updateDisplays() {
        // 更新數量顯示
        ItemMeta quantityMeta = quantityDisplay.getItemMeta();
        quantityMeta.setDisplayName(colors("&b數量：" + currentQuantity));
        List<String> quantityLore = new ArrayList<>();
        quantityLore.add(colors("&7點擊旁邊按鈕調整數量"));
        quantityLore.add(colors("&7或使用下方快速設置"));
        quantityMeta.setLore(quantityLore);
        quantityDisplay.setItemMeta(quantityMeta);

        // 更新確認按鈕
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        double totalCost = unitPrice * currentQuantity;
        confirmMeta.setDisplayName(colors("&a確認" + (isBuying ? "購買" : "出售")));
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(colors(""));
        confirmLore.add(colors("&7數量：&b" + currentQuantity));
        confirmLore.add(colors("&7單價：&6" + String.format("%,.2f", unitPrice) + " 元"));
        confirmLore.add(colors("&7總價：&6" + String.format("%,.2f", totalCost) + " 元"));
        confirmLore.add(colors(""));
        confirmLore.add(colors("&e點擊確認" + (isBuying ? "購買" : "出售")));
        confirmMeta.setLore(confirmLore);
        confirmButton.setItemMeta(confirmMeta);

        // 更新庫存顯示
        inventory.setItem(29, quantityDisplay);
        inventory.setItem(31, confirmButton);
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

        // 物品展示
        itemDisplay = bazaarItem.getIcon().clone();
        ItemMeta itemMeta = itemDisplay.getItemMeta();
        List<String> itemLore = new ArrayList<>();
        itemLore.add(colors(""));
        itemLore.add(colors("&6=== " + (isBuying ? "購買" : "出售") + "信息 ==="));
        itemLore.add(colors("&7單價：&6" + String.format("%.2f", unitPrice) + " 元"));
        itemLore.add(colors("&7操作：" + (isBuying ? "&a立即購買" : "&c立即出售")));
        itemMeta.setLore(itemLore);
        itemDisplay.setItemMeta(itemMeta);

        // 數量控制按鈕
        decreaseQuantity = new ItemStack(Material.RED_CONCRETE);
        ItemMeta decreaseQuantityMeta = decreaseQuantity.getItemMeta();
        decreaseQuantityMeta.setDisplayName(colors("&c數量 -1"));
        decreaseQuantity.setItemMeta(decreaseQuantityMeta);

        increaseQuantity = new ItemStack(Material.GREEN_CONCRETE);
        ItemMeta increaseQuantityMeta = increaseQuantity.getItemMeta();
        increaseQuantityMeta.setDisplayName(colors("&a數量 +1"));
        increaseQuantity.setItemMeta(increaseQuantityMeta);

        // 數量顯示
        quantityDisplay = new ItemStack(Material.IRON_NUGGET);
        ItemMeta quantityMeta = quantityDisplay.getItemMeta();
        quantityMeta.setDisplayName(colors("&b數量：" + currentQuantity));
        quantityDisplay.setItemMeta(quantityMeta);

        // 確認按鈕
        confirmButton = new ItemStack(isBuying ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK);
        ItemMeta confirmMeta = confirmButton.getItemMeta();
        confirmMeta.setDisplayName(colors(isBuying ? "&a確認購買" : "&c確認出售"));
        confirmButton.setItemMeta(confirmMeta);

        // 快速設置按鈕
        setQuantity1 = createQuickSetButton(1);
        setQuantity8 = createQuickSetButton(8);
        setQuantity16 = createQuickSetButton(16);
        setQuantity32 = createQuickSetButton(32);
        setQuantity64 = createQuickSetButton(64);

        // 初始化顯示
        //updateDisplays();
    }

    private ItemStack createQuickSetButton(int quantity) {
        ItemStack button = new ItemStack(Material.LIGHT_BLUE_CONCRETE);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(colors("&b設置為 " + quantity));
        List<String> lore = new ArrayList<>();
        lore.add(colors("&7快速設置數量為 " + quantity));
        lore.add(colors("&7總價：&6" + String.format("%.2f", unitPrice * quantity) + " 元"));
        meta.setLore(lore);
        button.setItemMeta(meta);
        return button;
    }
}