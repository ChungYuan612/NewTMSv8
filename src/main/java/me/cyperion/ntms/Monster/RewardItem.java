package me.cyperion.ntms.Monster;

import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

import static me.cyperion.ntms.Utils.colors;

public class RewardItem {

    private NewTMSv8 plugin;
    private ItemStack itemStack;
    private int min = 1,max = 1;
    private double dropChance;
    private static Random randomModifier = new Random();

    public RewardItem(NewTMSv8 plugin, ItemStack itemStack, double dropChance) {
        this.plugin = plugin;
        this.itemStack = itemStack;
        this.dropChance = dropChance;
    }

    public RewardItem(NewTMSv8 plugin, ItemStack itemStack, int min, int max, double dropChance) {
        this.plugin = plugin;
        this.itemStack = itemStack;
        this.min = min;
        this.max = max;
        this.dropChance = dropChance;
    }

    public void tryDropLoot(Player player) {
        double luckbouns = plugin.getPlayerData(player).getLuck();
        double value;
        if(luckbouns <= 0) value = dropChance;
        else value = dropChance * (1+luckbouns/100);
        double v = Math.random() * 100;
        if(v >= value) return;
        int amount = randomModifier.nextInt(max-min+1)+min;
        ItemStack item = this.itemStack.clone();
        Utils.giveItem(player, item, amount);
        System.out.println("[掉落] "+player+" 獲得了 "+itemStack.getItemMeta().getDisplayName()+ " value:"+v+" in 100(+luck:"+luckbouns+")");
        if(luckbouns > 0){
            player.sendMessage(colors("&6[掉落] &f"+itemStack.getItemMeta().getDisplayName()
                    +" &b("+(dropChance)+"&2+"+(value-dropChance)+"&b%)&f!"));
        }else{
            player.sendMessage(colors("&6[掉落] &f"+itemStack.getItemMeta().getDisplayName()
                    +" &b("+(dropChance)+"%)!"));
        }


    }

    public static boolean chanceIn(double rate){
        return Math.random()*101 < rate;
    }

}