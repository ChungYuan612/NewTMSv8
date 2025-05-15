package me.cyperion.ntms.Menu.Bazaar;

import me.cyperion.ntms.Bazaar.MarketItem;
import me.cyperion.ntms.Bazaar.TradingAPI;
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
    private List<MarketItem> marketItem;
    private final TradingAPI tradingAPI = new TradingAPI(plugin);

    public BazaarMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
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
        int solt = event.getSlot();
        if(solt == 31) player.closeInventory();
        if(mapToCenterIndex(solt) != -1) {
            player.sendMessage(colors("&6[錯誤] &c目前市場正在維修中..."));
            return;
            //MarketItem item = marketItem.get(mapToCenterIndex(solt)-1);

        }
    }

    @Override
    public void setMenuItems() {
        for(int i = 0; i < 4*9; i++){
            if(i == 31) inventory.setItem(i,close);
            else if(mapToCenterIndex(i)==-1) inventory.setItem(i,background);
            else if(mapToCenterIndex(i) < marketItem.size()){
                inventory.setItem(i,toDisplay(marketItem.get(mapToCenterIndex(i)).getItemStack()));
            }
        }
    }

    private ItemStack toDisplay(ItemStack itemStack){
        ItemStack item = itemStack.clone();
        try{
            ItemMeta meta =  item.getItemMeta();
            ArrayList<String> lore;
            if (meta.hasLore()) lore = (ArrayList<String>) meta.getLore();
            else lore = new ArrayList<>();
            lore.add(colors(""));
            String buyPriceString = "N/A";//TODO
            String sellPriceString = "N/A";//TODO
            lore.add(colors("&7最低賣出價格：&c"+ buyPriceString));
            lore.add(colors("&7最高買入價格：&c"+ sellPriceString));
            lore.add(colors(""));
            lore.add(colors("&e點擊查看更多"));


            meta.setLore(lore);
            item.setItemMeta(meta);

        }catch (Exception e){
            System.out.println("[Bazaar] 在顯示展示的物品時出現錯誤。toDisplay(ItemStack)");
        }

        return item;
    }

    private void init(){
        //背景
        background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        List<String> clickLore = new ArrayList<>();
        clickLore.add("");
        clickLore.add(colors("&e左鍵點擊"));
        //關閉按鈕
        close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(colors("&c關閉"));
        closeMeta.setLore(clickLore);
        close.setItemMeta(closeMeta);

        marketItem = new ArrayList<>();
        marketItem.add(new MarketItem(NTMSItems.REINFINED_LAPIS.name()
                , new ReinfinedLapis(plugin).toItemStack()));
        marketItem.add(new MarketItem(NTMSItems.EMERALD_COINS.name()
                , new Emerald_Coins().toItemStack()));
        marketItem.add(new MarketItem(NTMSItems.JADE_CORE.name()
                , new JadeCore().toItemStack()));


    }

    /**
     * 將 slot index（0~35）轉為中間 7x2 區域中的編號（1~14）
     * 從0開始
     * 若不在區域內，回傳 -1
     */
    private static int mapToCenterIndex(int slotIndex) {
        int row = slotIndex / 9;   // 第幾排（0～3）
        int col = slotIndex % 9;   // 第幾欄（0～8）

        // 中間區域只包含 row 1 和 2，col 1～7（排除邊緣）
        if ((row == 1 || row == 2) && (col >= 1 && col <= 7)) {
            int rowOffset = (row - 1) * 7;         // 第 2 排起始是 +0，第 3 排起始是 +7
            return rowOffset + (col - 1);      // col 1 對應 offset 0，加 1 就是編號
        }

        return -1; // 不在中心區域
    }
}