package me.cyperion.ntms.Command;

import me.cyperion.ntms.ItemStacks.Item.Item_InfiniteWindCharge;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.cyperion.ntms.Utils.colors;

/**
 * /Admin 管理員專用指令 使用方法請看說明書
 */
public class AdminCommand implements CommandExecutor {

    private NewTMSv8 plugin;

    public AdminCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)){
            return true;
        }
        Player player = (Player) commandSender;
        if(!player.isOp()){
            player.sendMessage(colors("&c只有管理員能用這個指令"));
            return true;
        }
        if(args.length == 0){
            PlayerData data = plugin.playerData.get(player);
            player.sendMessage(colors("&f您現在的數值為："));
            player.sendMessage(colors("&b魔力✯："+data.getMana()));
            player.sendMessage(colors("&b魔力回復速度✯："+data.getManaReg()));
            player.sendMessage(colors("&b魔力上限✯："+data.getMaxMana()));
            player.sendMessage(colors("&a突襲計算："+data.getRaidPoint()));
            player.sendMessage(colors("&d職業類別："+data.getClassType().toString()));
            player.sendMessage(colors("&6成就點數："+data.getAdvancePoint()));
            player.sendMessage(colors("&6現金："+plugin.getEconomy().getBalance(player)));
            return true;
        }else if ( args.length == 1){
            if(args[0].equals("wind")){
                ItemStack windCharge = new Item_InfiniteWindCharge().toItemStack();
                player.getInventory().addItem(windCharge);
            }
        }

        return true;
    }
}
