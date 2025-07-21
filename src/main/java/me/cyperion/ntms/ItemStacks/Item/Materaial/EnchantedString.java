package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.ItemStacks.ItemRegister;
import me.cyperion.ntms.ItemStacks.NTMSItemFactory;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

public class EnchantedString extends NTMSMaterial{
    public EnchantedString(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&7由&39&7條線壓縮而成的東西"));
        return lore;
    }

    @Override
    public Material getMaterailType() {
        return Material.STRING;
    }

    @Override
    public String getItemName() {
        return colors("&a附魔線");
    }

    @Override
    public int getCustomModelData() {
        return ItemRegister.CMD_ENCHANTED_STRING;
    }

    @Override
    public MaterialRate getMaterialRate() {
        return MaterialRate.NORMAL;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnchantedString"),this.toItemStack());
        recipe.shape("aaa","aaa","aaa");
        recipe.setIngredient('a', Material.STRING);
        return recipe;
    }
}
