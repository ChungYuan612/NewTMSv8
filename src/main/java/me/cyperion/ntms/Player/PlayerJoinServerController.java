package me.cyperion.ntms.Player;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinServerController implements Listener {

    public NewTMSv8 plugin;

    public PlayerJoinServerController(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        //記分板
        plugin.getTwPlayerSideBoard().createPlayerBoard(player);
        plugin.getTwPlayerSideBoard().refreshTimerLocation(player);

    }
}
