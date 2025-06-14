package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.Arrays;

import static me.cyperion.ntms.ItemStacks.ItemRegister.CMD_ENCHANTED_OBSIDIAN;
import static me.cyperion.ntms.Utils.colors;

public class EnchantedObsidian extends NTMSMaterial{
    public EnchantedObsidian(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        return new ArrayList<>( Arrays.asList(
                colors("&f由&a9&f個&7附魔黑曜石碎片&f製作而成"),
                colors("&f光滑的表面顯示了工藝的精緻")
        ));
    }

    @Override
    public Material getMaterailType() {
        return Material.OBSIDIAN;
    }

    @Override
    public String getItemName() {
        return colors("&8附魔黑曜石");
    }

    @Override
    public int getCustomModelData() {
        return CMD_ENCHANTED_OBSIDIAN;
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.RARE;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnchantedObsidian"),this.toItemStack());
        recipe.shape("xxx","xxx","xxx");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(new EnchantedObsidianPart(plugin).toItemStack()));
        return recipe;
    }
}

