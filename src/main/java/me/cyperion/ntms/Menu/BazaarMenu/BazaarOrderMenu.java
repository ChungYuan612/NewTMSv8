package me.cyperion.ntms.Menu.BazaarMenu;

import me.cyperion.ntms.Bazaar.MarketItem;
import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

/**
 * 訂單點進去準備下單的介面<br>
 * ロロロロロロロロロ
 * ロ一二ロ物ロ買賣ロ
 * ヒロロロロロロロロ
 */
public class BazaarOrderMenu extends Menu {

    private MarketItem item;

    private ItemStack background;
    private ItemStack goBack;

    private ItemStack instantBuy;
    private ItemStack instantSell;
    private ItemStack buyOrder;
    private ItemStack sellOrder;

    public BazaarOrderMenu(MarketItem item, PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
        this.item = item;
        init();
    }

    @Override
    public String getMenuName() {
        return item.getItemStack().getItemMeta().getDisplayName();
    }

    @Override
    public int getSolts() {
        return 3*9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.isLeftClick()){
            int solt = event.getSlot();
            if(solt == 18){
                BazaarMenu menu = new BazaarMenu(playerMenuUtility, plugin);
                menu.open();
            }else if(solt == 10){ //直接購買

            }
        }
    }

    @Override
    public void setMenuItems() {

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
        goBack = new ItemStack(Material.ARROW);
        ItemMeta goBackMeta = goBack.getItemMeta();
        goBackMeta.setDisplayName(colors("&a上一頁"));
        goBackMeta.setLore(clickLore);
        goBack.setItemMeta(goBackMeta);

        instantBuy = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta instantBuyMeta = instantBuy.getItemMeta();
        instantBuyMeta.setDisplayName(colors("&a即時購買"));
        instantBuyMeta.setLore(clickLore);
        instantBuy.setItemMeta(instantBuyMeta);

        instantSell = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta instantSellMeta = instantSell.getItemMeta();
        instantSellMeta.setDisplayName(colors("&a即時出售"));
        instantSellMeta.setLore(clickLore);
        instantSell.setItemMeta(instantSellMeta);

        buyOrder = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta buyOrderMeta = buyOrder.getItemMeta();
        buyOrderMeta.setDisplayName(colors("&a買單"));
        buyOrderMeta.setLore(clickLore);
        buyOrder.setItemMeta(buyOrderMeta);

        sellOrder = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
        ItemMeta sellOrderMeta = sellOrder.getItemMeta();
        sellOrderMeta.setDisplayName(colors("&a賣單"));
        sellOrderMeta.setLore(clickLore);
        sellOrder.setItemMeta(sellOrderMeta);

    }
}
