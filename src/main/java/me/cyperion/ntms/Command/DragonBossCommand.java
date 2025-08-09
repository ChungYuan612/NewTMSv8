package me.cyperion.ntms.Command;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DragonBossCommand implements CommandExecutor {
    private NewTMSv8 plugin;

    public DragonBossCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    // 管理員命令示例
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("draganboss")) {
            if (!sender.hasPermission("draganboss.admin")) {
                sender.sendMessage(ChatColor.RED + "你沒有權限使用此命令！");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage(ChatColor.YELLOW + "使用方法: /draganboss <start|end|status|stats>");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "start":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (plugin.getBossSystem().startBossFight(player.getWorld(), player.getLocation().add(0, 20, 0))) {
                            sender.sendMessage(ChatColor.GREEN + "BOSS戰已開始！");
                        } else {
                            sender.sendMessage(ChatColor.RED + "無法開始BOSS戰！(可能已有進行中的戰鬥或不在終界)");
                        }
                    }
                    break;

                case "end":
                    if (plugin.getBossSystem().forceEndBossFight()) {
                        sender.sendMessage(ChatColor.GREEN + "BOSS戰已強制結束！");
                    } else {
                        sender.sendMessage(ChatColor.RED + "目前沒有進行中的BOSS戰！");
                    }
                    break;

                case "status":
                    sender.sendMessage(plugin.getBossSystem().getBossStatus());
                    break;

                case "stats":
                    if (sender instanceof Player) {
                        sender.sendMessage(plugin.getBossSystem().getPlayerStats((Player) sender));
                    } else {
                        sender.sendMessage(ChatColor.RED + "只有玩家可以查看統計！");
                    }
                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "未知的子命令！");
            }
            return true;
        }
        return false;
    }
}
