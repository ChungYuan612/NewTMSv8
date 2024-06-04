package me.cyperion.ntms.Event;

import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import static me.cyperion.ntms.Utils.colors;

public class PlayerAdvanceDoneHandler implements Listener {


    private NewTMSv8 plugin;

    public PlayerAdvanceDoneHandler(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerAdvanceDone(PlayerAdvancementDoneEvent event){
        Player player = event.getPlayer();

        PlayerData playerData = plugin.getPlayerData(player);
        int point = playerData.getAdvancePoint();
        int addPoint = 0;
        switch (event.getAdvancement().getDisplay().getType()) {
            case CHALLENGE -> //挑戰成就
                    addPoint = 4 ;
            case GOAL -> //目標成就
                    addPoint = 2 ;
            case TASK -> //普通成就
                    addPoint = 1 ;
        }
        playerData.addAdvancePoint(point);
        player.sendMessage(colors("&a已獲得成就點數 &6" + addPoint + " &a點!"));
    }
}
