package me.cyperion.ntms;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

public class Utils {

    //轉換顏色文字
    public static String colors(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    public static void giveItem(Player player, ItemStack item, int amount) {
        item.setAmount(amount);
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
        if (!leftover.isEmpty()) {
            for (ItemStack remain : leftover.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), remain);
            }
        }
    }

    static Random rand = new Random();
    public static int curveGrowth(int x) {
        double a = 5;
        double b = 1.43;
        double baseValue = a * Math.pow(x, b);


        double fluctuation = 0.95 + (1.05 - 0.95) * rand.nextDouble();

        return (int)(baseValue * fluctuation);
    }

    //特殊字元
    public static final char[] allowedCharacters
            = new char[] { '/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};

    public static boolean isAllowedChatCharacter(char c0) {
        return c0 != 167 && c0 >= 32 && c0 != 127;
    }

    public static String SpecialChar(String s) {
        StringBuilder stringbuilder = new StringBuilder();
        char[] achar = s.toCharArray();
        int i = achar.length;

        for (int j = 0; j < i; ++j) {
            char c0 = achar[j];

            if (isAllowedChatCharacter(c0)) {
                stringbuilder.append(c0);
            }
        }
        return stringbuilder.toString();
    }

}
