package me.cyperion.ntms.Command;

import me.cyperion.ntms.ItemStacks.Armors.LapisArmor;
import me.cyperion.ntms.ItemStacks.Item.*;
import me.cyperion.ntms.ItemStacks.Item.Materaial.*;
import me.cyperion.ntms.ItemStacks.NTMSItems;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
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
            ItemStack item;
            if(name.equals(NTMSItems.RED_WAND.name())){
                item = new RedWand(plugin).toItemStack();
            }else if(name.equals(NTMSItems.JADE_CORE.name())){
                item = new JadeCore().toItemStack();
            }else if(name.equals(NTMSItems.EMERALD_COINS.name())) {
                item = new Emerald_Coins().toItemStack();
            }else if(name.equals(NTMSItems.INFINITE_WIND_CHARGE.name())) {
                item = new InfiniteWindCharge(plugin).toItemStack();
            }else if(name.equals(NTMSItems.ENCHANTED_SEEDS.name())) {
                item = new EnchantedSeeds(plugin).toItemStack();
            }else if(name.equals(NTMSItems.ENCHANTED_SUGAR.name())) {
                item = new EnchantedSugar(plugin).toItemStack();
            }else if(name.equals(NTMSItems.ENCHANTED_RED_STONE.name())) {
                item = new EnchantedRedstone(plugin).toItemStack();
            }else if(name.equals(NTMSItems.ENCHANTED_RED_STONE_BLOCK.name())) {
                item = new EnchantedRedstoneBlock(plugin).toItemStack();
            }else if(name.equals(NTMSItems.STOCK_3607.name())) {
                item = new Stocks(plugin).getItem(Stocks.StockType.s3607);
            }else if(name.equals(NTMSItems.STOCK_3230.name())) {
                item = new Stocks(plugin).getItem(Stocks.StockType.s3230);
            }else if(name.equals(NTMSItems.STOCK_3391.name())) {
                item = new Stocks(plugin).getItem(Stocks.StockType.s3391);
            }else if(name.equals(NTMSItems.STOCK_XAUD.name())) {
                item = new Stocks(plugin).getItem(Stocks.StockType.xaud);
            }else if(name.equals(NTMSItems.REINFINED_LAPIS.name())) {
                item = new ReinfinedLapis(plugin).toItemStack();
            }else{
                if(name.equals(NTMSItems.LAPIS_ARMOR.name())) {
                    ItemStack[] items = new LapisArmor(plugin).getItemStacks();
                    for(ItemStack i : items){
                        player.getInventory().addItem(i);
                    }
                    return true;
                }
                item = new ItemStack(Material.BARRIER);
            }
            player.getInventory().addItem(item);
            player.sendMessage("&a已給予 &2"+name);
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
        for(int i = 0; i < items.length; i++){
            options.add(items[i].name());
        }
    }
}
