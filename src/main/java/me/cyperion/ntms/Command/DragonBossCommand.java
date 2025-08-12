package me.cyperion.ntms.Command;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DragonBossCommand implements CommandExecutor, TabCompleter {
    private NewTMSv8 plugin;

    public DragonBossCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    // 管理員命令示例
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("dragonboss")) {
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
                case "repair":
                    plugin.getBossSystem().forceRepairSystem();
                    sender.sendMessage(ChatColor.GREEN + "強制修復系統完畢！");
                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "未知的子命令！");
            }
            return true;
        }
        return false;
    }


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("start");
            options.add("end");
            options.add("status");
            options.add("stats");
            options.add("repair");
            StringUtil.copyPartialMatches(args[0],options,completions);
        }
        return completions;
    }
}
