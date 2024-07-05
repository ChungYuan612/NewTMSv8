package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.ItemStacks.CraftRecipe;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

public class EnchantedSeeds extends NTMSMaterial {

    public EnchantedSeeds(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        return new ArrayList<>( Arrays.asList(
                colors("&f由&33&f組種子壓縮而成"),
                colors("&f感覺很營養(?")
        ));
    }

    @Override
    public Material getMaterailType() {
        return Material.WHEAT_SEEDS;
    }

    @Override
    public String getItemName() {
        return colors("&a附魔小麥種子");
    }

    @Override
    public int getCustomModelData() {
        return 4002;
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.NORMAL;
    }

    @Override
    public CraftRecipe getRecipe() {
        ItemStack item = new ItemStack(Material.WHEAT_SEEDS,64);
        ItemStack air = new ItemStack(Material.AIR);

        CraftRecipe recipe = new CraftRecipe(
                new ItemStack[][]{
                        {item,item,item},
                        {air,air,air},
                        {air,air,air}
                },toItemStack()
        );
        return recipe;
    }
}
