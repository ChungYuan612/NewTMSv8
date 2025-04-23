package me.cyperion.ntms.ItemStacks.Armors;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 在ItemRegister內註冊執行
 */
public interface PieceFullBouns {
    /**
     * 需自行檢查是否沒有裝備(null)
     */
    void checkAllArmor(Player player, ItemStack[] armors);

    boolean isFullSet(ItemStack[] armors);

    void addFullBouns(NewTMSv8 plugin, Player player);

    void removeFullBouns(NewTMSv8 plugin, Player player);
}