package me.cyperion.ntms.Player;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitServer implements Listener {

    private NewTMSv8 plugin;

    public PlayerQuitServer(NewTMSv8 plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerQuitServer(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.playerData.remove(player);

    }
}
