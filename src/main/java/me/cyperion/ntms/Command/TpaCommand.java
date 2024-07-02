package me.cyperion.ntms.Command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * 從舊台灣搬來的Tpa指令<br>
 * /tpa、/tpaccept /tpadeny 指令<br>
 * 關聯：NewTMSv8註冊。
 */
public class TpaCommand implements CommandExecutor {
    private static HashMap<UUID, UUID> tpa_player = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)) {
            return true;
        }else{
            if(command.getName().equalsIgnoreCase("tpa")){
                if(args.length!=1) {
                    player.sendMessage(ChatColor.RED + "插件用法:/tpa <玩家>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if(target!= null) {
                    tpa_player.put(target.getUniqueId(),player.getUniqueId() );
                    player.sendMessage(colors("&6[tpa] &d已發送傳送請求到&b"+target.getDisplayName()));
                    target.sendMessage(colors("&6[tpa] &b"+player.getDisplayName()+"&d想要傳送到你的位置"));
                    target.sendMessage(colors("&a如果你想讓&b"+player.getDisplayName()+"&a過來，請輸入 &e/tpaccept"));
                    target.sendMessage(colors("&c如果你想讓&b"+player.getDisplayName()+"&c滾的話就輸入 &e/tpadeny &c來拒絕"));
                    return true;
                }
                player.sendMessage(colors("&6[錯誤] &c找不到該玩家哦._."));
                return true;

            }else if(command.getName().equalsIgnoreCase("tpaccept")){
                //接受
                if(tpa_player.containsKey(player.getUniqueId())){
                    //找到可以傳送的
                    Player target = Bukkit.getPlayer(tpa_player.get(player.getUniqueId()));
                    player.sendMessage(colors("&6[tpa] &a已接受&b"+target.getDisplayName()+"&a的傳送邀請"));
                    target.sendMessage(colors("&6[tpa] &b"+player.getDisplayName()+"&a同意你傳送到他的位置"));
                    target.sendMessage(colors("&d傳送中..."));
                    target.teleport(player);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT,1,5);
                    tpa_player.remove(player.getUniqueId());
                    return true;
                }
            }else if(command.getName().equalsIgnoreCase("tpadeny")){
                //拒絕
                if (tpa_player.containsKey(player.getUniqueId())) {
                    Player target = Bukkit.getPlayer(tpa_player.get(player.getUniqueId()));
                    player.sendMessage(colors("&6[tpa] &c你已拒絕了"+target.getDisplayName()+"&c的傳送請求"));
                    target.sendMessage("&6[tpa] &b"+player.getDisplayName()+"&c不想要你傳送到他那裡");
                    tpa_player.remove(player.getUniqueId());
                    return true;
                }
            }

            player.sendMessage("&6[tpa] &c現在並沒有任何人想來你這裡哦!");
        }
        return true;
    }
}
