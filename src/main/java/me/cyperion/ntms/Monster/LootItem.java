package me.cyperion.ntms.Monster;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static me.cyperion.ntms.Utils.colors;

public class LootItem {
    private ItemStack itemStack;
    private int min = 1,max = 1;
    private double dropChance;
    private static Random randomModifier = new Random();

    public LootItem(ItemStack itemStack, double dropChance) {
        this.itemStack = itemStack;
        this.dropChance = dropChance;
    }

    public LootItem(ItemStack itemStack, int min, int max, double dropChance) {
        this.itemStack = itemStack;
        this.min = min;
        this.max = max;
        this.dropChance = dropChance;
    }
    public void tryDropLoot(Location location) {
        if(Math.random() * 101 > dropChance) return ;
        int amount = randomModifier.nextInt(max-min+1)+min;
        ItemStack item = this.itemStack.clone();
        item.setAmount(amount);
        location.getWorld().dropItemNaturally(location,item);
        Bukkit.getLogger().fine(colors("&6[掉落] &f"+itemStack.getItemMeta().getDisplayName()+" &b("+(dropChance)+"%)"));
    }

    public void tryDropLoot(Player player, Location location) {
        if(Math.random() * 101 > dropChance) return ;
        int amount = randomModifier.nextInt(max-min+1)+min;
        ItemStack item = this.itemStack.clone();
        item.setAmount(amount);
        location.getWorld().dropItemNaturally(location,item);
        player.sendMessage(colors("&6[掉落] &f"+itemStack.getItemMeta().getDisplayName()+" &b("+(dropChance)+"%)"));

    }

    public static boolean chanceIn(double rate){
        return Math.random()*101 < rate;
    }


}
