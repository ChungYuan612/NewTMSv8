package me.cyperion.ntms.Menu;

import me.cyperion.ntms.Class.ClassType;
import me.cyperion.ntms.Class.Explosion;
import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

import static me.cyperion.ntms.Utils.colors;

/**
 * 職業選擇介面 目前2個職業而已<br>
 * 關聯：/class指令註冊
 */
public class ClassSelectMenu extends Menu {
    ItemStack terminator,explosion,bard;

    public ClassSelectMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
    }

    @Override
    public String getMenuName() {
        return colors("&5選擇職業");
    }

    @Override
    public int getSolts() {
        return 9*3;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if(!(item.hasItemMeta() && item.getItemMeta().hasCustomModelData())){
            return;
        }
        if(item.getItemMeta().getCustomModelData() == 1008 ) { //TERMINATOR
            if(plugin.getPlayerData(player).getClassType() == ClassType.TERMINATOR) return;
            if(plugin.getPlayerData(player).getAdvancePoint() < 150) return;
            plugin.getPlayerData(player).setClassType(ClassType.TERMINATOR);
            updateMenu();
        }else if(item.getItemMeta().getCustomModelData() == 1009) { //EXPLOSION
            if(plugin.getPlayerData(player).getClassType() == ClassType.EXPLOSION) return;
            if(plugin.getPlayerData(player).getAdvancePoint() < 150) return;
            plugin.getPlayerData(player).setClassType(ClassType.EXPLOSION);
            updateMenu();

        }else if(item.getItemMeta().getCustomModelData() == 1010) { //BARD
            if(plugin.getPlayerData(player).getClassType() == ClassType.BARD) return;
            plugin.getPlayerData(player).setClassType(ClassType.BARD);
            updateMenu();
        }

    }//口口口口口口口口口

    private void updateMenu(){
        setupTerminator();
        setupExplosion();
        setupBard();
        inventory.setItem(11,terminator);
        inventory.setItem(13,bard);
        inventory.setItem(15,explosion);

    }
    @Override
    public void setMenuItems() {
        //背景
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        for(int i = 0; i < 27; i++) {
            inventory.setItem(i, background);
        }

        updateMenu();

    }

    private void setupTerminator(){
        terminator = plugin.getTerminator().getIcon();
        terminator = addSelectionLore(terminator,ClassType.TERMINATOR);
    }

    private void setupExplosion(){
        explosion = plugin.getExplosion().getIcon();
        explosion = addSelectionLore(explosion,ClassType.EXPLOSION);
    }

    private void setupBard(){
        bard = plugin.getBard().getIcon();
        bard = addSelectionLore(bard,ClassType.BARD);
    }

    //把物件增加選擇的lore
    private ItemStack addSelectionLore(ItemStack itemStack,ClassType classType){
        PlayerData data = plugin.getPlayerData(playerMenuUtility.getOwner());
        ItemMeta meta = itemStack.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) meta.getLore();
        if(data.getClassType().equals(classType)){
            lore.add(colors("&a您目前選擇這個職業"));
            meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        }else{
            lore.add(colors("&e點擊選擇職業"));
        }
        meta.setLore( lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return itemStack.clone();
    }
}
