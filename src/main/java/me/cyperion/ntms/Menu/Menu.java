package me.cyperion.ntms.Menu;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * 自訂GUI的最高層級的抽象類別<br>
 * 來源:KodySimpso EP.55教學
 */
public abstract class Menu implements InventoryHolder {

    protected Inventory inventory;

    protected PlayerMenuUtility playerMenuUtility;

    public Menu(PlayerMenuUtility utility) {
        this.playerMenuUtility = utility;
    }

    public abstract String getMenuName();

    public abstract int getSolts();

    public abstract void handleMenu(InventoryClickEvent event);

    public abstract void setMenuItems();

    public void open(){

        inventory = Bukkit.createInventory(this,getSolts(),getMenuName());

        this.setMenuItems();

        playerMenuUtility.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
