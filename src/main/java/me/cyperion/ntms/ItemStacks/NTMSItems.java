package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedRedstone;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public enum NTMSItems {
    ENCHANTED_RED_STONE,
    ENCHANTED_RED_STONE_BLOCK,
    ENCHANTED_SEEDS,
    ENCHANTED_SUGAR,
    ENCHANTED_ROTTEN,
    ENCHANTED_ENDER_PEARL,
    WIRED_ROTTEN,
    LOWER_WIRED_ROTTEN,
    JADE_CORE,
    INFINITE_WIND_CHARGE,
    RED_WAND,
    EMERALD_COINS,
    STOCK_3607,
    STOCK_3230,
    STOCK_3391,
    STOCK_XAUD,
    STOCK_3369,
    STOCK_5000,
    LAPIS_ARMOR,
    EMERALD_ARMOR,
    REINFINED_LAPIS,
    ENCHANTED_OBSIDIAN_PART,
    ENCHANTED_OBSIDIAN,
    GOLDEN_ESSENCE,
    PURE_GOLDEN_PICKAXE,
    PURE_GOLDEN_AXE,
    OBSIDIAN_CHESTPLATE,
    PURE_GOLDEN_DARK_SWORD,
    MYSTERY_TURTLE_EGG,
    ENCHANTED_STRING,
    TREASURE_CORE,
    EXPLOSION_BOW,
    ;
    public String getBazaarID(){
        return "NTMS_"+this.name();
    }

}
