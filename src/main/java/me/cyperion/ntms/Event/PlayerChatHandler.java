package me.cyperion.ntms.Event;

import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.cyperion.ntms.Utils.colors;

/**
 * 更改玩家聊天的訊息，格式:[???] Player: message <br>
 * 連結到NewTMSv8註冊而已
 */
public class PlayerChatHandler implements Listener {

    private NewTMSv8 plugin;

    public PlayerChatHandler(NewTMSv8 plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerChating(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();
        String prefix = getPerfix(player);
        String s = colors(plugin.getConfig().getStringList(player.getUniqueId().toString()).get(2));
        if(message.startsWith("&"))
            message = colors(message);
        event.setMessage(message.trim());
        event.setFormat(prefix +" "+ s + "%s" + ChatColor.WHITE + ": %s");

    }

    public String getPerfix(Player player){
        PlayerData playerData = plugin.playerData.get(player);
        String prefix;
        ChatColor color = ChatColor.GRAY;
        int point = playerData.getAdvancePoint();
        if (point > 185)
            color = ChatColor.DARK_RED;
        else if (point > 150)
            color = ChatColor.LIGHT_PURPLE;
        else if (point > 110)
            color = ChatColor.GOLD;
        else if (point > 70)
            color = ChatColor.DARK_PURPLE;
        else if (point > 40)
            color = ChatColor.BLUE;
        else if(point > 20)
            color = ChatColor.GREEN;

        prefix = colors("&f["+color+point+"&f]&r");

        return prefix;
    }
}
