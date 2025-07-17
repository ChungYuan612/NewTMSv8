package me.cyperion.ntms.Command;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.cyperion.ntms.Utils.colors;

/**
 * 給予其他玩家錢的指令<br>
 * 關聯：NewTMSv8註冊
 */
public class PayCommand implements CommandExecutor {

    private NewTMSv8 plugin;

    public PayCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player player){
            if(args.length == 2 || args.length == 3){
                if(Bukkit.getPlayer(args[0]) != null){
                    Player player2 = Bukkit.getPlayer(args[0]);
                    try{
                        Integer.parseInt(args[1]);
                    }catch (Exception e){
                        player.sendMessage(ChatColor.RED+"請輸入整數");
                        return true;
                    }
                    double pay = Double.parseDouble(args[1]);
                    if(pay <=0){
                        player.sendMessage(ChatColor.RED+"請輸入正整數");
                        return true;
                    }
                    if(player2.equals(player)){
                        player.sendMessage(colors("&6[錯誤] &c不能給自己錢哦"));
                        return true;
                    }

                    if(plugin.getEconomy().getBalance(player) >=  pay){

                        plugin.getEconomy().withdrawPlayer(player,pay);
                        plugin.getEconomy().depositPlayer(player2,pay);
                        String p1,p2;
                        p1 = "&6[pay] &a已將&e"+pay+"元&a給&b"+Bukkit.getPlayer(args[0]).getName()+"&a!";
                        p2 = "&6[pay] &a已收到&b"+player.getName()+"&a給的&e"+pay+"元&a!";
                        if(args.length == 3){
                            p1 += " &r&3備註: &f"+args[2];
                            p2 += " &r&3備註: &f"+args[2];
                        }
                        player.sendMessage(colors(p1));
                        player2.sendMessage(colors(p2));

                        return true;
                    }
                    player.sendMessage(ChatColor.RED+"你並沒有那麼多錢喔");
                    return true;
                }else{
                    player.sendMessage(ChatColor.RED+"找不到該玩家(或者是沒有上線)");
                    return true;
                }
            }else{
                player.sendRawMessage(ChatColor.RED+"用法: /pay <玩家> <金額> <備註(可空白)>");
                return true;
            }
        }

        return true;
    }
}
