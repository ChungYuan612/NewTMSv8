package me.cyperion.ntms.Menu;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;
//TODO
/**
 * <h2>台灣地圖的主要選單</h2>
 * 可以透過/menu來打開<br>
 * 目前就只有在實作試看看，照著KodySimpson EP.55的教學
 */
public class TWMainMenu extends Menu {

    public TWMainMenu(PlayerMenuUtility utility) {
        super(utility);
    }

    @Override
    public String getMenuName() {
        return colors("&8主選單");
    }

    @Override
    public int getSolts() {
        return 9*3;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {


    }

    @Override
    public void setMenuItems() {

        List<String> clickLore = new ArrayList<>();
        clickLore.add(colors("&e點擊傳送"));

        ItemStack goBed = new ItemStack(Material.RED_BED);
        ItemMeta goBedMeta = goBed.getItemMeta();
        goBedMeta.setDisplayName(colors("&d傳送至床重生點"));
        goBedMeta.setCustomModelData(101);//未來做資源包可用
        goBedMeta.setLore(clickLore);
        goBed.setItemMeta(goBedMeta);

        ItemStack background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

    }
}
