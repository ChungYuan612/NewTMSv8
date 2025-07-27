package me.cyperion.ntms.Player;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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




        if(plugin.getConfig().contains(player.getUniqueId().toString()))
            player.setPlayerListName(
                    colors(
                            plugin.getConfig().getStringList(player.getUniqueId().toString()).get(0)
                                    + player.getDisplayName()
                                    + plugin.getConfig().getStringList(player.getUniqueId().toString()).get(1))
            );
        else{
            player.setPlayerListName(ChatColor.GRAY+player.getDisplayName());
        }
        try{
            event.setJoinMessage(colors(
                    plugin.getConfig().getStringList(player.getUniqueId().toString()).get(0)
                            + player.getDisplayName() + plugin.getConfig().getStringList(player.getUniqueId().toString()).get(1)
                    + " &e加入了台灣ouo "
            ));
            Bukkit.broadcastMessage(colors("&6[公告]&a目前在線人數： (" + Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")"));
        }catch(Exception Ex){
            event.setJoinMessage(ChatColor.YELLOW +player.getDisplayName()+ " 加入了台灣ouo " + ChatColor.GREEN + "目前在線人數： (" + Bukkit.getServer().getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");
        }

        player.setPlayerListHeader(colors("&6&l新台灣地圖伺服器第8季 &r&a版本：JE 1.21.4"));
        player.setPlayerListFooter(colors("&b更多資訊歡迎加入我們的Discord社群!"));

        player.sendMessage(colors("&6[NTMS] &a歡迎來到台灣地圖伺服器！/ntms help 可以查詢所有指令"));

        //初始化或載入玩家資料到plugin.playerData的Map內。
        plugin.playerData.put(
                player,
                new PlayerData(plugin,player)
        );

        //記分板，只是為了顯示在底下，所以挪來底下觸發
        plugin.getTwPlayerSideBoard().refreshEventScoreboard(player);

    }
}
