package me.cyperion.ntms;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import static me.cyperion.ntms.Utils.colors;

public class Mana extends BukkitRunnable {

    private NewTMSv8 plugin;

    public Mana(NewTMSv8 plugin){
        this.plugin = plugin;
    }

    public final static double defaultMaxMana = 50;

    public void regenMana(Player player){
        double maxMana = plugin.getPlayerData(player).getMaxMana();
        double mana = plugin.getPlayerData(player).getMana();
        if(!hasMaxMana(player)){
            float regMana = 1f;
            //這裡之後處理回復魔力速度增快的程式
            //目前先一秒回復一點

            if(maxMana <= mana + regMana){
                //如果回復之後會超過
                plugin.getPlayerData(player).setMana(maxMana);
                return;
            }
            plugin.getPlayerData(player).setMana(regMana + mana);

        }
    }

    public boolean costMana(Player player, double amount){
        double mana = plugin.getPlayerData(player).getMana();
        boolean allowOverMana = plugin.getPlayerData(player).isAllowOverMana();
        if(mana - amount < 0){
            if(allowOverMana){
                plugin.getPlayerData(player).setMana(mana - amount);
                return true;
            }else{
                return false;
            }
        }else{
            plugin.getPlayerData(player).setMana(mana - amount);
        }
        return true;
    }

    public void addMana(Player player, double amount){
        double mana = plugin.getPlayerData(player).getMana();
        double maxMana = plugin.getPlayerData(player).getMaxMana();
        mana += amount;
        if(mana >= maxMana){
            plugin.getPlayerData(player).setMana(maxMana);
            return;
        }
        plugin.getPlayerData(player).setMana(mana);
    }

    public boolean hasMaxMana(Player player){
        double maxMana = plugin.getPlayerData(player).getMaxMana();
        double mana = plugin.getPlayerData(player).getMana();
        if(maxMana < mana){
            plugin.getPlayerData(player).setMana(maxMana);
            return true;
        }
        return maxMana <= mana;
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()){
            double maxMana = plugin.getPlayerData(player).getMaxMana();
            double mana = plugin.getPlayerData(player).getMana();
            regenMana(player);
            //顯示在玩家Actionbar上面 (之後程式再搬走)
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(
                    colors("&b&l魔力✯ " + (int)mana + "/" + (int)maxMana)
            ));
        }
    }
}
