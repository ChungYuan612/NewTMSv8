package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.ItemStacks.Item.InfiniteWindCharge;
import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedRedstone;
import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedRedstoneBlock;
import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedSeeds;
import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedSugar;
import me.cyperion.ntms.ItemStacks.Item.RedWand;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;

public class ItemRegister {

    private NewTMSv8 plugin;

    public ItemRegister(NewTMSv8 plugin) {
        this.plugin = plugin;
        register();
    }

    public void register() {
        registCraftItem();
        plugin.getServer().getPluginManager().registerEvents(new InfiniteWindCharge(plugin),plugin);

    }

    private void registCraftItem() {
        Bukkit.getServer().addRecipe(new EnchantedRedstone(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new EnchantedRedstoneBlock(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new RedWand(plugin).getRecipe());

        //plugin.getCraftHandler().getRecipes().add(new EnchantedSugar(plugin).getRecipe());
        //plugin.getCraftHandler().getRecipes().add(new EnchantedSeeds(plugin).getRecipe());
    }
}
