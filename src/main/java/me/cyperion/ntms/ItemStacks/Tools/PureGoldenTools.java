package me.cyperion.ntms.ItemStacks.Tools;

import me.cyperion.ntms.ItemStacks.Item.Materaial.MaterialRate;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

public abstract class PureGoldenTools  {

    public static final int CMD_PURE_GOLDEN_PICKAXE = 7001;
    public static final int CMD_PURE_GOLDEN_SWORD = 7002;

    public ItemStack itemStack;
    private NewTMSv8 plugin;

    public PureGoldenTools(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    protected abstract ArrayList<String> getLore() ;

    public abstract Material getMaterailType();


    public abstract int getCustomModelData();

    public MaterialRate getMaterialRate() {
        return MaterialRate.LEGENDARY;
    }

    public abstract String getTypeName();

    public abstract void otherSetup();

    public ItemStack getItem(){
        itemStack = new ItemStack(getMaterailType());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(colors("&6&l純金"+getTypeName()));
        ArrayList<String> lore = getLore();
        lore.add("");
        lore.add(colors(getMaterialRate().toLoreNoColor()+getTypeName()));
        meta.setLore(lore);
        //meta.setCustomModelData(getCustomModelData());
        itemStack.setItemMeta(meta);

        otherSetup();

        return itemStack.clone();
    }

    public ShapedRecipe toNMSRecipe(){
        return null;
    }
}
