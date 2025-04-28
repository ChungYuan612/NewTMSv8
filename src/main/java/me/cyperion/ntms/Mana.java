package me.cyperion.ntms;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

import static me.cyperion.ntms.Utils.colors;

/**
 * <h2>魔力系統</h2>
 * 主管所有魔力裝置 需要與 PlayerData 互動<br>
 * 由 NewTMSv8 主程式控制
 */
public class Mana extends BukkitRunnable {

    private NewTMSv8 plugin;
    public Mana(NewTMSv8 plugin){
        this.plugin = plugin;
    }

    public final static double defaultMaxMana = 50;

    /**
     * 自然回復魔力
     * @param player 玩家
     */
    public void regenMana(Player player){
        double maxMana = plugin.getPlayerData(player).getMaxMana();
        double mana = plugin.getPlayerData(player).getMana();
        double manaReg = plugin.getPlayerData(player).getManaReg();
        if(!hasMaxMana(player)){
            double regMana = manaReg;
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

    /**
     * 扣掉魔力
     * @param player 玩家
     * @param amount 魔力數量
     * @param allowOverMana 是否允許變負數
     * @return 有沒有執行成功(魔力是否夠扣)
     */
    public boolean costMana(Player player, double amount,boolean allowOverMana){
        double mana = plugin.getPlayerData(player).getMana();

        if(mana - amount < 0){
            if(allowOverMana && amount == 400){ // EXPLOSION
                plugin.getPlayerData(player).setMana(mana - amount);
                return true;
            }else{
                showManaNotEnough(player);
                return false;
            }
        }else{
            plugin.getPlayerData(player).setMana(mana - amount);
        }
        return true;
    }

    public boolean costMana(Player player, double amount){
        return costMana(player,amount,false);
    }

    /**
     * 非自然魔力回復(可以指定數量)
     * @param player 玩家
     * @param amount 魔力數量
     */
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

    /**
     * 檢查該玩家是否達到魔力上限
     * @param player
     * @return 是否魔力滿了
     */
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
            if (!plugin.getPlayerData(player).getShowManaOnActionbar())
                continue;
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(
                    colors("&b魔力✯ " + (int)mana + "/" + (int)maxMana)
            ));
        }
    }

    public void showManaNotEnough(Player player){
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,new TextComponent(
                colors("&c&l魔力不足")
        ));
        player.playSound(player.getLocation(),
                Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
    }
}
