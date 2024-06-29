package me.cyperion.ntms.Command;

import me.cyperion.ntms.Menu.Menu;
import me.cyperion.ntms.Menu.PlayerMenuUtility;
import me.cyperion.ntms.Menu.TWMainMenu;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.cyperion.ntms.Utils.colors;

/**
 * /Menu指令<br>
 * 關聯只有在NewTMSv8
 */
public class MenuCommand implements CommandExecutor {

    private NewTMSv8 plugin;

    public MenuCommand(NewTMSv8 plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!(sender instanceof Player))
            return true;

        if(args.length != 0){
            sender.sendMessage(colors("&c欸幹，你連一個/menu都打不好嗎?給我這麼多參數衝尛~"));
            return true;
        }

        Player player = (Player) sender;

        Menu menu = new TWMainMenu(new PlayerMenuUtility(player),plugin);
        menu.open();


        return true;
    }
}
