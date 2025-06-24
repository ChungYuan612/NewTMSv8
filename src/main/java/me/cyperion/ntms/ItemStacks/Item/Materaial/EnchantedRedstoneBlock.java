package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Arrays;

import static me.cyperion.ntms.ItemStacks.ItemRegister.CMD_ENCHANTED_RED_STONE_BLOCK;
import static me.cyperion.ntms.Utils.colors;

public class EnchantedRedstoneBlock extends NTMSMaterial{

    public EnchantedRedstoneBlock(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        return new ArrayList<>( Arrays.asList(
                colors("&f由&a9&f個附魔紅石壓縮而成"),
                colors("&f散發著強烈的紅色光芒")
        ));
    }

    @Override
    public Material getMaterailType() {
        return Material.REDSTONE_BLOCK;
    }

    @Override
    public String getItemName() {
        return colors("&9附魔紅石磚");
    }

    @Override
    public int getCustomModelData() {
        return CMD_ENCHANTED_RED_STONE_BLOCK;
    }

    @Override
    public MaterialRate getMaterialRate() {
        return MaterialRate.RARE;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnchantedRedstoneBlock"),this.toItemStack());
        recipe.shape("xxx","xxx","xxx");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(new EnchantedRedstone(plugin).toItemStack()));
        return recipe;
    }
}
