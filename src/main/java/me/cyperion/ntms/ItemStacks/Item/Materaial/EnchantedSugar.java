package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.ItemStacks.CraftRecipe;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

/**
 * 附魔蔗糖物件 目前無法合成
 */
public class EnchantedSugar extends NTMSMaterial {

    public EnchantedSugar(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    public Material getMaterailType() {
        return Material.SUGAR;
    }

    @Override
    protected ArrayList<String> getLore() {
        return new ArrayList<>( Arrays.asList(
                colors("&f由&39組&f甘蔗精練而成"),
                colors("&f味道像是來到了台南~")
        ));
    }

    @Override
    public String getItemName() {
        return colors("&b附魔蔗糖");
    }

    @Override
    public int getCustomModelData() {
        return 4001;
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.RARE;
    }

    @Override
    public CraftRecipe getRecipe() {
        ItemStack item = new ItemStack(Material.SUGAR_CANE,64);
        CraftRecipe recipe = new CraftRecipe(
                new ItemStack[][]{
                        {item,item,item},
                        {item,item,item},
                        {item,item,item}
                },toItemStack()
        );
        return recipe;
    }
}
