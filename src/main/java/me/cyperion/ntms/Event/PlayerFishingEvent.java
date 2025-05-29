package me.cyperion.ntms.Event;

import me.cyperion.ntms.ItemStacks.Item.JadeCore;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Random;

import static me.cyperion.ntms.Utils.colors;

public class PlayerFishingEvent implements Listener {

    private NewTMSv8 plugin;

    private final Random random = new Random();
    private final JadeCore jadeCore = new JadeCore();

    public PlayerFishingEvent(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event){
        if(!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
            return;
        double value = random.nextDouble(0,100);
        double rate = 0.85d;
        if(!event.getPlayer().getLocation().getWorld().isClearWeather())
            rate = 1d;
        //防止釣魚機
        if(!event.getHook().isInOpenWater())
            rate /=2;

        Player player = event.getPlayer();
        double base = rate;
        double luckbouns = plugin.getPlayerData(player).getLuck();
        if(luckbouns <= 0) value = rate;
        else value = rate * (1+luckbouns/100);

        if(value<=rate){
            System.out.println(event.getPlayer().getDisplayName() + " 釣起了 碎玉核心! "+ value + "in 100");
            if(event.getCaught() instanceof Item item){
                item.setItemStack(jadeCore.toItemStack().clone());
                player.sendMessage(colors("&6[稀有釣魚] &f"+item.getItemStack().getItemMeta().getDisplayName()+" &b("+(base)+"&2+"+luckbouns+"&b%)"));

            }
        }
    }
}
