package me.cyperion.ntms.ItemStacks.Item;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * <h2>無限風彈</h2>
 * 簡單做一下物品，還沒有架構<br>
 * 預計售價30萬<br>
 * 關聯只有ItemRegister
 */
public class InfiniteWindCharge implements Listener {

    ItemStack infinite_WindCharge;
    double manaCost = 2;
    private NewTMSv8 plugin;

    public InfiniteWindCharge(NewTMSv8 plugin) {
        this.plugin = plugin;
        setupItem();
    }

    protected void setupItem(){
        infinite_WindCharge = new ItemStack(Material.CLAY_BALL);
        ItemMeta meta = infinite_WindCharge.getItemMeta();
        meta.setDisplayName(colors("&3無限風彈"));
        ArrayList<String> lores = new ArrayList<>();
        lores.add(colors("&f可以無限使用的風彈。"));
        lores.add(colors("&2冷卻時間&f: 0.5&as"));
        lores.add(colors("&b魔力消耗&f: &b"+manaCost));
        lores.add(colors(""));
        lores.add(colors("&e右鍵使用"));
        meta.setLore(lores);
        meta.setCustomModelData(2001);
        infinite_WindCharge.setItemMeta(meta);
    }

    public ItemStack toItemStack(){
        if(infinite_WindCharge == null)
            setupItem();
        return infinite_WindCharge;
    }

    private Map<UUID,Long> cooldown = new HashMap<>();
    @EventHandler
    public void onPlayerShootingWindCharge(PlayerInteractEvent event){
        if(!plugin.enableMana) return;
        Player player = event.getPlayer();
        if(!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))){
            return;
        }
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if(item.getItemMeta() != null && item.getItemMeta().hasCustomModelData()
                && item.getItemMeta().getCustomModelData() == 2001){
            UUID uuid = player.getUniqueId();
            if(!cooldown.containsKey(uuid)){
                cooldown.put(uuid,System.currentTimeMillis());
            }
            long lastTimeShoot = cooldown.get(uuid);
            if(System.currentTimeMillis() < lastTimeShoot + 500)
                return;

            //扣除魔力
            if(!plugin.getMana().costMana(player,manaCost)) return;

            //讓玩家丟出風彈
            player.getWorld().spawn(
                    player.getEyeLocation(),
                    WindCharge.class,
                    windCharge ->
                            windCharge.setVelocity(player.getLocation().getDirection())
            );
            cooldown.replace(uuid,System.currentTimeMillis());
        }
    }

}
