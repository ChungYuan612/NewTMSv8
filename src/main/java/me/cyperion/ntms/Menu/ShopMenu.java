package me.cyperion.ntms.Menu;

import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

/**
 * 商店GUI<br>
 * 可以在這裡跟系統購買東西，包含無限風彈<br>
 * 關聯: MenuCommand
 */
public class ShopMenu extends Menu {

    private ItemStack background;
    private ItemStack close;
    private ItemStack previousPage;
    private ItemStack nextPage;
    private ItemStack pageInfo;

    private ShopItem[] shopItems;
    private int currentPage = 1;
    private int itemsPerPage = 28; // 7x4區域 = 28個物品
    private int totalPages;

    public ShopMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&2商城 &7(第" + currentPage + "頁)");
    }

    @Override
    public int getSolts() {
        return 9*6;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        int slot = event.getSlot();

        // 關閉按鈕
        if(slot == 49) {
            playerMenuUtility.getOwner().closeInventory();
            return;
        }

        // 上一頁按鈕
        if(slot == 45) {
            if(currentPage > 1) {
                currentPage--;
                setMenuItems();
                updateTitle();
            }
            return;
        }

        // 下一頁按鈕
        if(slot == 53) {
            if(currentPage < totalPages) {
                currentPage++;
                setMenuItems();
                updateTitle();
            }
            return;
        }

        // 商品點擊處理
        int centerIndex = mapToCenterIndex(slot);
        if(centerIndex == -1) return;

        // 計算實際商品索引 (基於當前頁面)
        int actualIndex = (currentPage - 1) * itemsPerPage + centerIndex - 1;
        if(actualIndex >= shopItems.length) return;

        ShopItem item = shopItems[actualIndex];
        Player player = (Player) event.getWhoClicked();

        if(event.isLeftClick()){
            // 購買邏輯
            if(item.buyPrice == -1) return;
            double price = item.buyPrice;
            int amount = 1;
            if(event.isShiftClick()) {
                amount = 64;
                price *= 64;
            }
            if(this.plugin.getEconomy().getBalance(player) >= price && player.getInventory().firstEmpty() != -1){
                this.plugin.getEconomy().withdrawPlayer(player, price);
                ItemStack finalItem = item.item.clone();
                // 專門處理自己的頭顱
                if(finalItem.getType() == Material.PLAYER_HEAD){
                    SkullMeta meta = (SkullMeta) finalItem.getItemMeta();
                    meta.setDisplayName(colors("&b"+player.getName()));
                    meta.setOwningPlayer(player);
                    finalItem.setItemMeta(meta);
                }
                giveItem(player, finalItem, amount);
                player.sendMessage(colors("&6[成交] &a你購買了&2"+item.getItemName(this.plugin)+"&a共花費&6"+price+"&a元"));
            }else{
                player.sendMessage(colors("&6[警告] &c你沒有足夠的錢買這些東西或背包已滿喔._."));
            }
        }else if(event.isRightClick()){
            // 售賣邏輯
            if(item.sellPrice == -1) return;
            double price = item.sellPrice;
            int amount = 1;
            if(event.isShiftClick()){
                amount = 64;
            }
            if(hasItem(player, item.item.clone(), amount)){
                this.plugin.getEconomy().depositPlayer(player, price*amount);
                removeItem(player, item.item.clone(), amount);
                player.sendMessage(colors("&6[成交] &a你賣出了&2"+item.getItemName(this.plugin)+"&a共賺入&6"+price*amount+"&a元"));
            }else{
                player.sendMessage(colors("&6[警告] &c你沒有足夠的東西賣喔._."));
            }
        }
    }

    @Override
    public void setMenuItems() {
        // 清空所有物品
        inventory.clear();

        // 設置背景
        for(int i = 0; i < 6*9; i++){
            inventory.setItem(i, background);
        }

        // 設置商品
        int startIndex = (currentPage - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, shopItems.length);

        for(int i = 0; i < itemsPerPage; i++){
            int slot = getCenterSlot(i + 1);
            if(slot != -1 && (startIndex + i) < endIndex) {
                inventory.setItem(slot, shopItems[startIndex + i].toDisplay(this.plugin));
            }
        }

        // 設置控制按鈕
        inventory.setItem(49, close); // 關閉按鈕

        // 上一頁按鈕 (只在不是第一頁時顯示)
        if(currentPage > 1) {
            inventory.setItem(45, previousPage);
        }

        // 下一頁按鈕 (只在不是最後一頁時顯示)
        if(currentPage < totalPages) {
            inventory.setItem(53, nextPage);
        }

        // 頁面資訊
        inventory.setItem(48, getPageInfoItem());
    }

    private void init(){
        // 背景
        background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        shopItems = ShopItem.values();
        totalPages = (int) Math.ceil((double) shopItems.length / itemsPerPage);

        List<String> clickLore = new ArrayList<>();
        clickLore.add("");
        clickLore.add(colors("&e左鍵點擊"));

        // 關閉按鈕
        close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(colors("&c關閉"));
        closeMeta.setLore(clickLore);
        close.setItemMeta(closeMeta);

        // 上一頁按鈕
        previousPage = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = previousPage.getItemMeta();
        prevMeta.setDisplayName(colors("&a上一頁"));
        List<String> prevLore = new ArrayList<>();
        prevLore.add("");
        prevLore.add(colors("&7點擊切換到上一頁"));
        prevMeta.setLore(prevLore);
        previousPage.setItemMeta(prevMeta);

        // 下一頁按鈕
        nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPage.getItemMeta();
        nextMeta.setDisplayName(colors("&a下一頁"));
        List<String> nextLore = new ArrayList<>();
        nextLore.add("");
        nextLore.add(colors("&7點擊切換到下一頁"));
        nextMeta.setLore(nextLore);
        nextPage.setItemMeta(nextMeta);
    }

    /**
     * 取得頁面資訊物品
     */
    private ItemStack getPageInfoItem() {
        ItemStack pageInfoItem = new ItemStack(Material.PAPER);
        ItemMeta meta = pageInfoItem.getItemMeta();
        meta.setDisplayName(colors("&6頁面資訊"));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(colors("&7當前頁面: &e" + currentPage));
        lore.add(colors("&7總頁數: &e" + totalPages));
        lore.add(colors("&7總商品數: &e" + shopItems.length));
        meta.setLore(lore);
        pageInfoItem.setItemMeta(meta);
        return pageInfoItem;
    }

    /**
     * 更新GUI標題 (需要重新開啟GUI才能生效)
     */
    private void updateTitle() {
        // 由於Bukkit限制，標題更新需要重新開啟GUI
        // 這裡只是設置，實際標題會在下次打開時顯示
    }

    public void removeItem(Player player, ItemStack itemToRemove, int amount) {
        ItemStack clone = itemToRemove.clone();
        clone.setAmount(amount);
        player.getInventory().removeItem(clone);
    }

    public void giveItem(Player player, ItemStack item, int amount) {
        item.setAmount(amount);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        if (!leftover.isEmpty()) {
            for (ItemStack remain : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), remain);
            }
        }
    }

    public boolean hasItem(Player player, ItemStack item, int amount) {
        return player.getInventory().containsAtLeast(item, amount);
    }

    /**
     * 將 slot index（0~53）轉為中間 7x4 區域中的編號（1~28）
     * 若不在區域內，回傳 -1
     */
    public static int mapToCenterIndex(int slotIndex) {
        int row = slotIndex / 9;   // 第幾排（0～5）
        int col = slotIndex % 9;   // 第幾欄（0～8）

        // 中間區域包含 row 1 ~ 4，col 1～7（排除邊緣）
        if ((row >= 1 && row <= 4) && (col >= 1 && col <= 7)) {
            int rowOffset = (row - 1) * 7;         // 第 2 排起始是 +0，第 3 排起始是 +7
            return rowOffset + (col - 1) + 1;      // col 1 對應 offset 0，加 1 就是編號
        }

        return -1; // 不在中心區域
    }

    /**
     * 根據中心區域編號（1~28）取得對應的slot位置
     */
    private int getCenterSlot(int centerIndex) {
        if(centerIndex < 1 || centerIndex > 28) return -1;

        int adjustedIndex = centerIndex - 1; // 轉為0-based
        int row = adjustedIndex / 7 + 1;      // 第幾排 (1-4)
        int col = adjustedIndex % 7 + 1;      // 第幾欄 (1-7)

        return row * 9 + col; // 轉換為實際slot位置
    }
}