package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.ItemStacks.ItemRegister;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 附魔終界珍珠 9個終界珍珠合成
 */
public class EnchantedEnderPearl extends NTMSMaterial {
    public EnchantedEnderPearl(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&7由&a9&7個終界珍珠合成的東西"));
        lore.add(colors("&7可以合成一些有趣的東西"));
        return lore;
    }

    @Override
    public Material getMaterailType() {
        return Material.ENDER_PEARL;
    }

    @Override
    public String getItemName() {
        return colors("&a附魔終界珍珠");
    }

    @Override
    public int getCustomModelData() {
        return ItemRegister.CMD_ENCHANTED_ENDER_PEARL;
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.NORMAL;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnchantedEnderPearl"),this.toItemStack());
        recipe.shape("xxx","xxx","xxx");
        recipe.setIngredient('x',Material.ENDER_PEARL);
        return recipe;
    }
}