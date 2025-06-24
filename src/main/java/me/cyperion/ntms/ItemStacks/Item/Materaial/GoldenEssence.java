package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.ItemStacks.ItemRegister;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

public class GoldenEssence extends NTMSMaterial {
    public GoldenEssence(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&f由&d突襲活動&f中低機率獲得的東西"));
        lore.add(colors("&f可以用和合成各類&6白金&f的物品"));
        return lore;
    }

    @Override
    public Material getMaterailType() {
        return Material.GOLD_NUGGET;
    }

    @Override
    public String getItemName() {
        return colors("&e純金元素");
    }

    @Override
    public int getCustomModelData() {
        return ItemRegister.CMD_GOLDEN_ESSENCE;
    }

    @Override
    public MaterialRate getMaterialRate() {
        return MaterialRate.RARE;
    }

}
