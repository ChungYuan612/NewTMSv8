package me.cyperion.ntms.Menu;

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

public class WarpMenu extends Menu {

    private ItemStack background;
    private ItemStack close;
    private ItemStack goBed;
    private ItemStack goResource;
    private ItemStack goTW;
    private ItemStack goEnd;

    public WarpMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&d傳送介面");
    }

    @Override
    public int getSolts() {
        return 9*3;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (item.isSimilar(goBed)) {
            player.performCommand("warp bed");
        }else if ( item.isSimilar(goResource)) {
            player.performCommand("warp rs");
        }else if ( item.isSimilar(goTW)) {
            player.performCommand("warp tw");
        }else if ( item.isSimilar(goEnd)) {
            player.performCommand("warp end");
        }
    }

    @Override
    public void setMenuItems() {


        for(int i = 0; i < 27; i++){
            inventory.setItem(i,background);
        }

        inventory.setItem(10,goBed.clone());
        inventory.setItem(11,goTW.clone());
        inventory.setItem(12,goResource.clone());
        inventory.setItem(13,goEnd.clone());
        inventory.setItem(22,close.clone());


    }
    private void init(){

        List<String> clickLore = new ArrayList<>();
        clickLore.add("");
        clickLore.add(colors("&e左鍵點擊"));

        //背景
        background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        //關閉按鈕
        close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(colors("&c關閉"));
        closeMeta.setLore(clickLore);
        close.setItemMeta(closeMeta);

        //床重生點
        goBed = new ItemStack(Material.RED_BED);
        ItemMeta goBedMeta = goBed.getItemMeta();
        goBedMeta.setDisplayName(colors("&d傳送至床重生點  &8/warp bed"));
        goBedMeta.setCustomModelData(1011);//未來做資源包可用
        goBedMeta.setLore(clickLore);
        goBed.setItemMeta(goBedMeta);

        //資源界傳送
        goResource = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta goResourceMeta = goResource.getItemMeta();
        goResourceMeta.setDisplayName(colors("&d傳送至資源界 &8/warp rs"));
        goResourceMeta.setCustomModelData(1012);//未來做資源包可用
        goResourceMeta.setLore(clickLore);
        goResource.setItemMeta(goResourceMeta);

        //傳送至TW
        goTW = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta goTWMeta = goTW.getItemMeta();
        goTWMeta.setDisplayName(colors("&d傳送至臺灣地圖 &8/warp tw"));
        goTWMeta.setCustomModelData(1013);//未來做資源包可用
        goTWMeta.setLore(clickLore);
        goTW.setItemMeta(goTWMeta);

        //傳送至TW
        goEnd = new ItemStack(Material.END_STONE);
        ItemMeta goEndMeta = goTW.getItemMeta();
        goEndMeta.setDisplayName(colors("&d傳送至終界 &8/warp end"));
        goEndMeta.setCustomModelData(1014);//未來做資源包可用
        goEndMeta.setLore(clickLore);
        goTW.setItemMeta(goTWMeta);
    }
}
