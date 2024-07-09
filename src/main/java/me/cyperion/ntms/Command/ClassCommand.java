package me.cyperion.ntms.Command;

import me.cyperion.ntms.Menu.ClassSelectMenu;
import me.cyperion.ntms.Menu.Menu;
import me.cyperion.ntms.Menu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.cyperion.ntms.Utils.colors;

/**
 * 選擇職業的指令/class 打開介面，有OP的話不需要成就點數150點
 * 關聯：去ClassSelectMenu取得介面、NewTMSv8註冊
 */
public class ClassCommand implements CommandExecutor {
    private NewTMSv8 plugin;

    public ClassCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player))
            return true;
        PlayerData data = plugin.getPlayerData(player);
        int adv = data.getAdvancePoint();

        if(adv < 150 && !player.isOp()){
            player.sendMessage(colors("&6[提示] &c職業需要成就點數&a150&c點才開放喔"));
            return true;
        }

        Menu menu = new ClassSelectMenu(new PlayerMenuUtility(player),plugin);
        menu.open();
        return true;
    }
}
