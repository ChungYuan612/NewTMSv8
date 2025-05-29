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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

/**
 * 商店GUI<br>
 * 可以在這裡跟系統購買東西，包含無限風彈<br>
 * 關聯: 還在做，所以沒有
 */
public class ShopMenu extends Menu {

    private ItemStack background;
    private ItemStack close;

    //private final List<ShopItem> shopItems = new ArrayList<>();
    private ShopItem[] shopItems;
    public ShopMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&2商城");
    }

    @Override
    public int getSolts() {
        return 9*5;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        int solt = event.getSlot();
        if(solt == 31)
            playerMenuUtility.getOwner().closeInventory();
        int centerIndex = mapToCenterIndex(solt);
        if(centerIndex == -1) return;
        if(centerIndex > shopItems.length) return;
        ShopItem item = shopItems[centerIndex-1];
        Player player = (Player) event.getWhoClicked();
        //player.sendMessage(String.valueOf(centerIndex));
        if(event.isLeftClick()){
            if(item.buyPrice == -1) return;
            double price = item.buyPrice;
            int amount = 1;
            if(event.isShiftClick()) {
                amount = 64;
                price *= 64;
            }
            if(this.plugin.getEconomy().getBalance(player) >= price && player.getInventory().firstEmpty() != -1){
                this.plugin.getEconomy().withdrawPlayer(player, price);
                giveItem(player, item.item.clone(),amount);
                player.sendMessage(colors("&6[成交] &a你購買了&2"+item.getItemName(this.plugin)+"&a共花費&6"+price+"&a元"));
            }else{
                player.sendMessage(colors("&6[警告] &c你沒有足夠的錢買這些東西或背包已滿喔._."));
            }
        }else if(event.isRightClick()){
            if(item.sellPrice == -1) return;
            double price = item.sellPrice;
            int amount=1;
            if(event.isShiftClick()){
                amount = 64;
            }
            if(hasItem(player,item.item.clone(),amount)){
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
        for(int i = 0; i < 5*9; i++){
            if(i == 40){
                inventory.setItem(i,close);
            }else if(mapToCenterIndex(i) == -1)
                inventory.setItem(i,background);
            else if(mapToCenterIndex(i) <= shopItems.length){
                inventory.setItem(i,shopItems[mapToCenterIndex(i)-1].toDisplay(this.plugin));
            }

        }
    }

    private void init(){
        //背景
        background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);
        shopItems = ShopItem.values();

        List<String> clickLore = new ArrayList<>();
        clickLore.add("");
        clickLore.add(colors("&e左鍵點擊"));
        //關閉按鈕
        close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(colors("&c關閉"));
        closeMeta.setLore(clickLore);
        close.setItemMeta(closeMeta);

    }

    public void removeItem(Player player, ItemStack itemToRemove, int amount) {
        ItemStack clone = itemToRemove.clone();
        clone.setAmount(amount);
        player.getInventory().removeItem(clone);
    }


    public void giveItem(Player player, ItemStack item,int amount) {
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
     * 將 slot index（0~35）轉為中間 7x2 區域中的編號（1~14）
     * 若不在區域內，回傳 -1
     */
    public static int mapToCenterIndex(int slotIndex) {
        int row = slotIndex / 9;   // 第幾排（0～3）
        int col = slotIndex % 9;   // 第幾欄（0～8）

        // 中間區域只包含 row 1 ~ 3，col 1～7（排除邊緣）
        if ((row >= 1 && row <= 3) && (col >= 1 && col <= 7)) {
            int rowOffset = (row - 1) * 7;         // 第 2 排起始是 +0，第 3 排起始是 +7
            return rowOffset + (col - 1) + 1;      // col 1 對應 offset 0，加 1 就是編號
        }

        return -1; // 不在中心區域
    }



}
