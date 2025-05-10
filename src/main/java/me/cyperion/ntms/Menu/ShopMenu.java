package me.cyperion.ntms.Menu;

import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 商店GUI<br>
 * 目前還在做<br>
 * 可以在這裡跟系統購買東西，包含無限風彈<br>
 * 關聯: 還在做，所以沒有
 */
public class ShopMenu extends Menu {

    public ShopMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
    }

    @Override
    public String getMenuName() {
        return colors("&a商城");
    }

    @Override
    public int getSolts() {
        return 9*4;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

    }

    @Override
    public void setMenuItems() {

    }


}
