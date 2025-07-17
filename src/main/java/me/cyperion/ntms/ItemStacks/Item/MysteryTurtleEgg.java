package me.cyperion.ntms.ItemStacks.Item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

public class MysteryTurtleEgg {
    ItemStack item;
    public MysteryTurtleEgg() {
        init();
    }
    private void init(){
        item = new ItemStack(Material.TURTLE_EGG,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colors("&8&l&o神秘的海龜蛋"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors(""));
        lore.add(colors("&7不知為何在這出現的"));
        lore.add(colors("&7神秘海龜蛋"));
        lore.add(colors(""));
        lore.add(colors("&c&l取得方式：&r&7釣魚"));
        meta.setLore(lore);
        meta.setCustomModelData(5001);
        item.setItemMeta(meta);

    }
    public ItemStack toItemStack(){
        return item.clone();
    }
}
