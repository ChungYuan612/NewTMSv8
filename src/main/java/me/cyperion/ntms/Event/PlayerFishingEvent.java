package me.cyperion.ntms.Event;

import me.cyperion.ntms.ItemStacks.Item.JadeCore;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Random;

public class PlayerFishingEvent implements Listener {
    private final Random random = new Random();
    private final JadeCore jadeCore = new JadeCore();
    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event){
        if(!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
            return;
        double value = random.nextDouble(0,100);
        double rate = 0.85d;
        if(!event.getPlayer().getLocation().getWorld().isClearWeather())
            rate = 1d;
        if(value<=rate){
            System.out.println(event.getPlayer().getDisplayName() + " 釣起了 碎玉核心! "+ value + "in 100");
            if(event.getCaught() instanceof Item item)
                item.setItemStack(jadeCore.toItemStack().clone());
        }
    }
}
