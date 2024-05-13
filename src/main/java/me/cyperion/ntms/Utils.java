package me.cyperion.ntms;

import org.bukkit.ChatColor;

public class Utils {



    //轉換顏色文字
    public static String colors(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
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
