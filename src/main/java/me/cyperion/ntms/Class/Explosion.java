package me.cyperion.ntms.Class;

import me.cyperion.ntms.ItemStacks.Item.RedWand;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static me.cyperion.ntms.Utils.colors;

public class Explosion extends Class implements Listener {
    public Explosion(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    public ClassType getClassType() {
        return ClassType.EXPLOSION;
    }

    @Override
    public String getName() {
        return colors("&cエクスプロージョン");
    }

    public void onPlayerExplosionReady(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Player player = event.getPlayer();
            ItemStack wand  = new RedWand(plugin).toItemStack();//TODO 紅魔法杖
            if(player.getInventory().getItemInMainHand().equals(wand)){

            }
        }
    }
}
