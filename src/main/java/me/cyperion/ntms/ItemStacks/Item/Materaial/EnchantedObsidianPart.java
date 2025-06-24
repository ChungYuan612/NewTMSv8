package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Arrays;

import static me.cyperion.ntms.ItemStacks.ItemRegister.CMD_ENCHANTED_OBSIDIAN_PART;
import static me.cyperion.ntms.ItemStacks.ItemRegister.CMD_ENCHANTED_RED_STONE;
import static me.cyperion.ntms.Utils.colors;

public class EnchantedObsidianPart extends NTMSMaterial{
    public EnchantedObsidianPart(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        return new ArrayList<>( Arrays.asList(
                colors("&f由&a9&f個黑曜石製作而成，過程中"),
                colors("&f損耗嚴重只剩碎片的加工物。")
        ));
    }

    @Override
    public Material getMaterailType() {
        return Material.FLINT;
    }

    @Override
    public String getItemName() {
        return colors("&7附魔黑曜石碎片");
    }

    @Override
    public int getCustomModelData() {
        return CMD_ENCHANTED_OBSIDIAN_PART;
    }

    @Override
    public MaterialRate getMaterialRate() {
        return MaterialRate.NORMAL;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnchantedObsidianPart"),this.toItemStack());
        recipe.shape("xxx","xxx","xxx");
        recipe.setIngredient('x',Material.OBSIDIAN);
        return recipe;
    }
}
