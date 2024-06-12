package me.cyperion.ntms.Command;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

import static me.cyperion.ntms.Utils.colors;

public class WarpCommand implements CommandExecutor {

    private NewTMSv8 plugin;

    public WarpCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    private Random random = new Random();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(colors("&c需要是玩家才能使用此指令"));
            return true;
        }
        Player player = (Player) sender;
        if(args.length == 1){
            String where = args[0];

            switch (where.toLowerCase()) {
                case "resource", "rs" -> {
                    World w = plugin.getResourceWorld();
                    Location rs = w.getSpawnLocation()
                            .add(random.nextInt(4, 20000),
                                    0, random.nextInt(4, 20000));
                    int y = w.getHighestBlockYAt(rs);
                    rs.setY(y);
                    player.teleport(rs);
                    player.sendMessage(colors("&6[提示] &d正在傳送至資源界。"));
                }
                case "bed" -> {
                    Location bed = player.getBedSpawnLocation();
                    if (bed == null) {
                        player.sendMessage(colors("&c找不到床重生點!"));
                        break;
                    }
                    player.teleport(bed);
                    player.sendMessage(colors("&6[提示] &d傳送至床重生點。"));
                }
                case "taiwan", "tw", "taipei" -> {
                    World world = plugin.getTWWorld();
                    Location teleport = world.getSpawnLocation();

                    if (player.getRespawnLocation() != null &&
                            player.getRespawnLocation().getWorld().getName().equals(plugin.MAIN_WORLD_NAME)) {
                        teleport = player.getRespawnLocation();
                    }
                    player.teleport(teleport);
                    player.sendMessage(colors("&6[提示] &d傳送至台灣地圖。"));
                }
            }
        }else{
            player.sendMessage(colors("&c使用方法(無視大小寫)：/warp <資源界/床/台灣> "));
            player.sendMessage(colors("&2資源界：rs,resource"));
            player.sendMessage(colors("&7床：bed"));
            player.sendMessage(colors("&6台灣：tw,taiwan"));
        }

        return false;
    }
}
