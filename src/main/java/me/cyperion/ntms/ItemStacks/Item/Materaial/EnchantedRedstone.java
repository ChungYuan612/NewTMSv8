package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Arrays;

import static me.cyperion.ntms.Utils.colors;

public class EnchantedRedstone extends NTMSMaterial{

    public EnchantedRedstone(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        return new ArrayList<>( Arrays.asList(
                colors("&f由&a6&f個紅石磚壓縮而成"),
                colors("&f散發著紅色光芒")
        ));
    }

    @Override
    public Material getMaterailType() {
        return Material.REDSTONE;
    }

    @Override
    public String getItemName() {
        return colors("&a附魔紅石");
    }

    @Override
    public int getCustomModelData() {
        return 4003;
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.NORMAL;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnchantedRedstone"),this.toItemStack());
        recipe.shape("xxx","xxx","   ");
        recipe.setIngredient('x',Material.REDSTONE_BLOCK);
        return recipe;
    }
}
