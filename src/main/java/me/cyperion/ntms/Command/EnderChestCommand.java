package me.cyperion.ntms.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import static me.cyperion.ntms.Utils.colors;

/**
 * 可以隨時開啟終界箱 <br>
 * /enderchest (沒有參數)
 */
public class EnderChestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(colors("&c只有玩家才能輸入此指令"));
            return false;
        }
        Player player = (Player) sender;
        InventoryView view = player.openInventory(player.getEnderChest());
        view.setTitle(colors("&5終界箱"));
        player.sendMessage(colors("&5正在開啟終界箱"));
        return true;
    }
}
