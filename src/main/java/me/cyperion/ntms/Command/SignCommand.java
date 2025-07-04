package me.cyperion.ntms.Command;

import me.cyperion.ntms.NSKeyRepo;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

public class SignCommand implements CommandExecutor {

    private final NewTMSv8 plugin;

    public SignCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player player) {
            try{

                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore() || !itemStack.getEnchantments().isEmpty() || itemStack.getType() == Material.SHULKER_BOX){
                    player.sendMessage(colors("&c該物品不是原版物品或特殊物品"));
                    return true;
                }
                ItemStack item = new ItemStack(itemStack.getType(),itemStack.getAmount());
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(colors(itemStack.getItemMeta().getDisplayName()));
                ArrayList<String> lore = new ArrayList<>();
                lore.add(colors("&6[&f簽名&6]&f: &b"+player.getName()));
                meta.setLore(lore);
                meta.getPersistentDataContainer().set(
                        plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ITEM_SIGNED_NAME),
                        PersistentDataType.STRING,
                        player.getUniqueId().toString()
                );
                item.setItemMeta(meta);
                player.getInventory().setItemInMainHand(item);
                player.sendMessage(colors("&6[提示] &a簽名物品成功"));
            }catch (Exception e){
                System.out.println(e.toString());
                player.sendMessage(colors("&c/sign指令程式執行時出錯，嘗試別的方法或和中原講吧。"));
            }
        }
        return false;
    }


}
