package me.cyperion.ntms.Monster;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

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

    }

    public static boolean chanceIn(double rate){
        return Math.random()*101 < rate;
    }


}
