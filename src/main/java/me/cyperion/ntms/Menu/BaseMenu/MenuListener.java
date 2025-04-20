package me.cyperion.ntms.Menu.BaseMenu;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * <h2>注意：這個類別是Event但是我特別放到Menu來</h2>
 * 主要掌控玩家點擊自訂的GUI，通知Menu <br>
 * 來源:KodySimpso EP.55教學
 */
public class MenuListener implements Listener {

    @EventHandler
    public void onPlayerClickEvent(InventoryClickEvent event){

        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null)
            return;
        InventoryHolder holder = event.getClickedInventory().getHolder();

        if(holder instanceof Menu){
            event.setCancelled(true);
            if(event.getCurrentItem() == null){
                return;
            }

            Menu menu = (Menu) holder;
            menu.handleMenu(event);
        }

    }
}
