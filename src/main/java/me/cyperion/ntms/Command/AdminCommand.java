package me.cyperion.ntms.Command;

import me.cyperion.ntms.ItemStacks.Armors.EmeraldArmor;
import me.cyperion.ntms.ItemStacks.Armors.LapisArmor;
import me.cyperion.ntms.ItemStacks.Item.*;
import me.cyperion.ntms.ItemStacks.Item.Materaial.*;
import me.cyperion.ntms.ItemStacks.NTMSItems;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import me.cyperion.ntms.Utils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

/**
 * /Admin 管理員專用指令 使用方法請看說明書
 * /admin 取得詳細資訊
 * /admin <ItemName> 取得該物品
 */
public class AdminCommand implements CommandExecutor, TabCompleter {

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
            player.sendMessage(colors("&5幸運點數："+data.getLuck()));
            player.sendMessage(colors("&2累積簽到："+data.getSignInCount()));
            player.sendMessage(colors("&6現金："+plugin.getEconomy().getBalance(player)));
            return true;
        }else if ( args.length == 1){
            String name = args[0];
            if(name.equalsIgnoreCase("Mana")){
                plugin.getPlayerData(player).setMana(plugin.getPlayerData(player).getMaxMana());
                return true;
            }
            ItemStack item;
            item = plugin.getFactory().getNTMSItem(name);

            if(item != null || item.getType() != Material.BARRIER){
                player.getInventory().addItem(item);
                player.sendMessage(colors("&a已給予 &2"+name));
            }
            ItemStack[] items = new ItemStack[4];
            if(args[0].equalsIgnoreCase(NTMSItems.EMERALD_ARMOR.name())){
                 items = new EmeraldArmor(plugin).getItemStacks();
            }else if(args[0].equalsIgnoreCase(NTMSItems.LAPIS_ARMOR.name())){
                items = new LapisArmor(plugin).getItemStacks();
            }
            for(int i = 0;i<items.length;i++){
                if(items[i] != null)
                    Utils.giveItem(player,items[i],1);
            }

        }
        return true;
    }

    List<String> options = new ArrayList<>();
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> completions = new ArrayList<>();

        if(args.length == 1) {
            if(options.isEmpty())
                generateOptions();
            StringUtil.copyPartialMatches(args[0],options,completions);
        }
        return completions;
    }

    private void generateOptions() {
        NTMSItems[] items = NTMSItems.values();
        for (NTMSItems item : items) {
            options.add(item.name());
        }
        options.add("Mana");
    }

}
