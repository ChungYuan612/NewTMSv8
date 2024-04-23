package me.cyperion.ntms.Player;

import me.cyperion.ntms.NewTMSv8;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

public class PlayerData {
    private float maxMana,mana;

    /**
     * 取得玩家的特殊資料(Mana之類的)
     * @param player 玩家
     * @return 玩家特殊資料
     */
    public static PlayerData getPlayerData(NewTMSv8 plugin, Player player){
        PlayerData data = new PlayerData();
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.has();
        //TODO
    }

    public void savePlayerData()
}
