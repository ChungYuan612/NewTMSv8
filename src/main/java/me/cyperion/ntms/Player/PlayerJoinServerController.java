package me.cyperion.ntms.Player;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.cyperion.ntms.Utils.colors;

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
        plugin.getTwPlayerSideBoard().refreshTimer(player,true);

        player.setPlayerListHeader("&6&l新台灣地圖伺服器第8季 &r&a版本："+plugin.getServer().getVersion());
        player.setPlayerListHeader("&b更多資訊歡迎加入我們的Discord社群!");

        player.sendMessage(colors("&6[NTMS] &a歡迎來到台灣地圖伺服器！/ntms help 可以查詢所有指令"));

        //初始化或載入玩家資料到plugin.playerData的Map內。
        plugin.playerData.put(
                player,
                new PlayerData(plugin,player)
        );

    }
}
