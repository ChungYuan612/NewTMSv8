package me.cyperion.ntms.Event;

import me.cyperion.ntms.ItemStacks.Item.JadeCore;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Random;

public class PlayerFishingEvent implements Listener {
    private Random random = new Random();
    private JadeCore jadeCore = new JadeCore();
    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event){
        if(!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
            return;
        if(random.nextDouble(0,100)<0.85d){
            if(event.getCaught() instanceof Item item)
                item.setItemStack(jadeCore.toItemStack().clone());
        }
    }
}
