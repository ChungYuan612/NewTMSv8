package me.cyperion.ntms.Class;

import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.entity.Player;

@Deprecated
public abstract class ClassUpgrade {
    protected NewTMSv8 plugin;

    public ClassUpgrade(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    int getPerkMaxLevel(int perk){
        return 3;
    }

    /**
     *
     * @param data PlayerData
     * @param whichPerk First是1，Second是2，Third是3
     * @return 0沒解鎖，1為升級到一階
     */
    int getUnlockPerk(PlayerData data, int whichPerk){

        switch (whichPerk){
            case 1:
                return data.getPerkFirst();
            case 2:
                return data.getPerkSecond();
            case 3:
                return data.getPerkThird();
            default:
                return 0;
        }
    }

    int getUnlockPerk(Player player, int whichPerk){
        return getUnlockPerk(
                plugin.playerData.get(player),
                whichPerk);
    }




}
