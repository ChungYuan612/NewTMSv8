package me.cyperion.ntms.ItemStacks.Tools;

import me.cyperion.ntms.ItemStacks.Item.Materaial.GoldenEssence;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

public class PureGoldenAxe extends PureGoldenTools {
    private final NewTMSv8 plugin;

    public PureGoldenAxe(NewTMSv8 plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    protected ArrayList<String> getLore() {
        ArrayList<String > lore = new ArrayList<>();
        lore.add(colors("&f純&6金&f打造而成的高效率金斧，真的"));
        lore.add(colors("&f挖得很快，而且還不會壞"));
        return lore;
    }

    @Override
    public Material getMaterailType() {
        return Material.GOLDEN_PICKAXE;
    }

    @Override
    public int getCustomModelData() {
        return PureGoldenTools.CMD_PURE_GOLDEN_PICKAXE;
    }

    @Override
    public String getTypeName() {
        return "斧頭";
    }

    @Override
    public void otherSetup() {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setUnbreakable(true);
        itemStack.setItemMeta(meta);
        itemStack.addUnsafeEnchantment(Enchantment.EFFICIENCY,6);
    }

    @Override
    public ShapedRecipe toNMSRecipe(){
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"PureGoldenAxe"),this.getItem());
        recipe.shape("xx ","xa "," a ");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(new GoldenEssence(plugin).toItemStack()));
        recipe.setIngredient('a', Material.STICK);
        return recipe;
    }
}
