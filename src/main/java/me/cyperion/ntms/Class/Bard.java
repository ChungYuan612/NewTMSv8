package me.cyperion.ntms.Class;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static me.cyperion.ntms.Utils.colors;

public class Bard extends Class implements Listener {

    public Bard(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    public ClassType getClassType() {
        return ClassType.BARD;
    }

    @Override
    public String getName() {
        return colors("&f吟遊詩人");
    }

    //當玩家吹號角
    public void onPlayerBlowHorn(PlayerInteractEvent event) {
        // 避免觸發兩次（副手與主手）
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.GOAT_HORN) return;

        List<Entity> entities = player.getNearbyEntities(8, 8, 8);
        // ✅ 給予藥水效果
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 10, 1));         // 加速 II，持續10秒
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1));   // 再生 II，持續5秒

        // 顯示提示訊息
        player.sendMessage("§a你吹響了號角，獲得了力量！");

    }


}