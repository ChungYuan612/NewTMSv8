package me.cyperion.ntms.ItemStacks.Tools;

import me.cyperion.ntms.ItemStacks.Item.Materaial.GoldenEssence;
import me.cyperion.ntms.ItemStacks.Item.Materaial.MaterialRate;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

public class PureGoldenPickaxe {
    private final NewTMSv8 plugin;
    private ItemStack itemStack;

    public PureGoldenPickaxe(NewTMSv8 plugin) {
        this.plugin = plugin;
        initialize();
    }
    private void initialize(){
        itemStack = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(colors("&6&l純金十字稿"));
        ArrayList<String > lore = new ArrayList<>();
        lore.add(colors("&f純&6金&f打造而成的高效率金稿，雖然挖"));
        lore.add(colors("&f不了礦，但挖一些石頭等建材還是得心應手的!"));
        lore.add(colors(""));
        lore.add(colors(MaterialRate.LEGENDARY.toLoreNoColor()+"十字稿"));
        meta.setLore(lore);
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);
        itemStack.addUnsafeEnchantment(Enchantment.EFFICIENCY,6);

    }

    public ItemStack getItemStack() {
        return itemStack;
    }
    public ShapedRecipe getNMSRecipe(){
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"PureGoldenPickaxe"),this.getItemStack());
        recipe.shape("xxx"," a "," a ");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(new GoldenEssence(plugin).toItemStack()));
        recipe.setIngredient('a', Material.STICK);
        return recipe;
    }
}
