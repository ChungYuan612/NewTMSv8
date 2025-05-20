package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

public class EnchantedRotten extends NTMSMaterial {

    public EnchantedRotten(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&7由&a6&7個腐肉合成，有點臭的東西"));

        return lore;
    }

    @Override
    public Material getMaterailType() {
        return Material.ROTTEN_FLESH;
    }

    @Override
    public String getItemName() {
        return colors("&a附魔腐肉");
    }

    @Override
    public int getCustomModelData() {
        return 4008;
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.NORMAL;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnchantedRotten"),this.toItemStack());
        recipe.shape("xxx","xxx","   ");
        recipe.setIngredient('x',Material.ROTTEN_FLESH);
        return recipe;
    }
}