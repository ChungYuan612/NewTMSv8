package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.ItemStacks.Armors.LapisArmor;
import me.cyperion.ntms.ItemStacks.Armors.PieceFullBouns;
import me.cyperion.ntms.ItemStacks.Item.InfiniteWindCharge;
import me.cyperion.ntms.ItemStacks.Item.Materaial.*;
import me.cyperion.ntms.ItemStacks.Item.RedWand;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ItemRegister {

    private NewTMSv8 plugin;

    public ItemRegister(NewTMSv8 plugin) {
        this.plugin = plugin;
        allPieceFullBouns.add(new LapisArmor(plugin));
        register();
    }

    private List<PieceFullBouns> allPieceFullBouns = new ArrayList<>();
    private void registerPieceorFullBouns() {
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            for(PieceFullBouns pieceFullBouns : allPieceFullBouns) {
                pieceFullBouns.checkAllArmor(player,player.getInventory().getArmorContents());
                if(pieceFullBouns.isFullSet(player.getInventory().getArmorContents()))
                    pieceFullBouns.addFullBouns(plugin,player);
                else
                    pieceFullBouns.removeFullBouns(plugin,player);
            }
        }

    }

    public void register() {
        registCraftItem();
        plugin.getServer().getPluginManager().registerEvents(new InfiniteWindCharge(plugin),plugin);
        plugin.getServer().getPluginManager().registerEvents(new WiredRotten(plugin),plugin);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                registerPieceorFullBouns();
            }
        };
        runnable.runTaskTimer(plugin,0L,10L);
    }

    private void registCraftItem() {
        Bukkit.getServer().addRecipe(new EnchantedEnderPearl(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new EnchantedRotten(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new WiredRotten(plugin).toNMSRecipe());

        Bukkit.getServer().addRecipe(new EnchantedRedstone(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new EnchantedRedstoneBlock(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new RedWand(plugin).getRecipe());

        LapisArmor lapisArmor = new LapisArmor(plugin);
        ShapedRecipe[] recipe = lapisArmor.toNMSRecipe();
        for(int i = 0; i<4;i++)
            Bukkit.getServer().addRecipe(recipe[i]);


        //plugin.getCraftHandler().getRecipes().add(new EnchantedSugar(plugin).getRecipe());
        //plugin.getCraftHandler().getRecipes().add(new EnchantedSeeds(plugin).getRecipe());
    }
}
