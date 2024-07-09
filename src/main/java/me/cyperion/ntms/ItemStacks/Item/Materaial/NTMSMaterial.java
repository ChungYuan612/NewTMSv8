package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.ItemStacks.CraftRecipe;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

/**
 * 台灣素材的物品父類別
 */
public abstract class NTMSMaterial {

    protected NewTMSv8 plugin;
    protected ItemStack itemStack;

    public NTMSMaterial(NewTMSv8 plugin) {
        this.plugin = plugin;
        setUp();
    }

    public void setUp(){
        itemStack = new ItemStack(getMaterailType());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(getItemName());
        List<String> lore = getLore();
        lore.add(colors(""));
        lore.add(colors(getMaterailRate().toLoreNoColor()+"素材"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(getCustomModelData());
        itemStack.setItemMeta(meta);
    }

    protected abstract ArrayList<String> getLore();

    public abstract Material getMaterailType();

    public abstract String getItemName();

    public abstract int getCustomModelData();

    public abstract MaterailRate getMaterailRate();

    public ItemStack toItemStack() {
        return itemStack;
    }

    @Nullable
    public CraftRecipe getRecipe(){
        return null;
    }

    public ShapedRecipe toNMSRecipe(){
        return null;
    }



    public enum MaterailRate{
        NORMAL("&2","普通"),
        RARE("&3","稀有"),
        EPIC("&5","罕見");
        String color;
        String rateName;
        MaterailRate(String color,String rateName) {
            this.color = color;
            this.rateName = rateName;
        }

        public String toLoreNoColor(){
            return color+rateName;
        }
    }
}
