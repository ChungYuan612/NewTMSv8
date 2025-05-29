package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 從主類繼承而來 wired rotten破咒肉塊
 */
public class LowerWiredRotten extends WiredRotten {

    public LowerWiredRotten(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&f由各種意義上的東西組合成的簡單肉塊，"));
        lore.add(colors("&f味道聞到後讓人有點反胃...，但吃下去"));
        lore.add(colors("&f消耗&64點飢餓值&f會回復&c8點血量&f..."));
        lore.add(colors(""));
        lore.add(colors("&8這個肉不能以一般方法吃掉喔。"));
        return lore;
    }

    @Override
    public String getItemName() {
        return colors("&d低階破咒肉塊");
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.NORMAL;
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"LowerWiredRotten"),toItemStack());
        recipe.shape(
                "xox",
                "o o",
                "xox");
        recipe.setIngredient('o', Material.ENDER_PEARL);
        recipe.setIngredient('x', Material.ROTTEN_FLESH);
        return recipe;
    }
}
