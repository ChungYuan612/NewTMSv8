package me.cyperion.ntms.Command;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * 每次簽到指令/signin<br>
 * 關聯：NewTMSv8 註冊
 */
public class SigninCommand implements CommandExecutor {

    private NewTMSv8 plugin;

    public static final LinkedList<UUID> signinedList = new LinkedList<>();

    public SigninCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player))
            return true;
        if(plugin.UNDER_MAINTENANCE){
            player.sendMessage("&c伺服器正在下線維修");
            return true;
        }
        if(signinedList.contains(player.getUniqueId())){
            player.sendMessage(colors("&6[錯誤] &c您已經簽到過了喔"));
            return true;
        }
        //簽到成功
        int money = 1500;
        plugin.getEconomy().depositPlayer(player,money);
        plugin.getPlayerData(player).addSignInCount(1);//未來可以拿這個來做新功能
        player.sendMessage(colors("&6[提示] &a成功簽到，本次簽到獲得&6"+money+"&a元! 總共累積簽到 &3"
                +plugin.getPlayerData(player).getSignInCount()+" &a次!"));
        signinedList.add(player.getUniqueId());

        return true;
    }
}
