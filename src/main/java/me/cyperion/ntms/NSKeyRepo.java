package me.cyperion.ntms;

import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

/**
 * 這個類別專門放所有的NameSpaceKey
 * 使用字串常數可以快速取得NameSpaceKey
 */
public class NSKeyRepo {

    private final Map<String, NamespacedKey> keyMap;
    public static final String KEY_PD_ADVANCE_POINT = "player_data_advance_point";
    public static final String KEY_PD_LUCK = "player_data_luck";
    public static final String KEY_PD_MANA = "player_data_mana";
    public static final String KEY_PD_MAX_MANA = "player_data_max_mana";
    public static final String KEY_PD_MANA_REG = "player_data_mana_reg";
    public static final String KEY_PD_CLASS_TYPE = "player_data_class_type";
    public static final String KEY_PD_PERK_FIRST = "player_data_perk_first";
    public static final String KEY_PD_PERK_SECOND = "player_data_perk_second";
    public static final String KEY_PD_PERK_THIRD = "player_data_perk_third";
    public static final String KEY_PD_ALLOW_OVER_MANA = "player_data_allow_over_mana";
    public static final String KEY_PD_RAID_POINT = "player_data_raid_point";
    public static final String KEY_PD_SHOW_MANA = "player_data_show_mana";
    public static final String KEY_PD_TOTAL_SIGNIN_COUNT = "player_data_total_signin_count";
    public static final String KEY_PD_CRIT_CHANCE = "player_data_crit_chance";
    public static final String KEY_PD_CRIT_DAMAGE = "player_data_crit_damage";

    public static final String KEY_ITEM_STOCK_ID = "item_stock_id";
    public static final String KEY_ITEM_STOCK_LAST_REWARD = "item_stock_last_reward";
    public static final String KEY_ITEM_SIGNED_NAME = "item_signed_name";//TODO

    public static final String KEY_ARMOR_NAME = "key_armor_name";
    public static final String KEY_ARMOR_MANA_ADD = "key_armor_mana_add";
    public static final String KEY_ARMOR_LUCK_ADD = "key_armor_mana_add"; //!!!這裡有BUG 但不能修 TODO
    public static final String KEY_ARMOR_LUCK_ADD_FIX = "key_armor_luck_add";
    public static final String KEY_ARMOR_CRIT_CHANCE_ADD = "key_armor_crit_chance_add";
    public static final String KEY_ARMOR_CRIT_DAMAGE_ADD = "key_armor_crit_damage_add";


    public NSKeyRepo() {
        this.keyMap = new HashMap<>();

        keyMap.put(KEY_PD_LUCK, NamespacedKey.minecraft(KEY_PD_LUCK));
        //double

        keyMap.put(KEY_PD_MANA, NamespacedKey.minecraft(KEY_PD_MANA));
        //double

        keyMap.put(KEY_PD_MAX_MANA, NamespacedKey.minecraft(KEY_PD_MAX_MANA));
        //double

        keyMap.put(KEY_PD_MANA_REG, NamespacedKey.minecraft(KEY_PD_MANA_REG));
        //double

        keyMap.put(KEY_PD_CLASS_TYPE, NamespacedKey.minecraft(KEY_PD_CLASS_TYPE));
        //string

        keyMap.put(KEY_PD_PERK_FIRST, NamespacedKey.minecraft(KEY_PD_PERK_FIRST));
        //string
        keyMap.put(KEY_PD_PERK_SECOND, NamespacedKey.minecraft(KEY_PD_PERK_SECOND));
        //string
        keyMap.put(KEY_PD_PERK_THIRD, NamespacedKey.minecraft(KEY_PD_PERK_THIRD));
        //string

        keyMap.put(KEY_PD_SHOW_MANA, NamespacedKey.minecraft(KEY_PD_SHOW_MANA));
        //boolean

        keyMap.put(KEY_PD_ALLOW_OVER_MANA, NamespacedKey.minecraft(KEY_PD_ALLOW_OVER_MANA));
        //boolean
        keyMap.put(KEY_PD_RAID_POINT, NamespacedKey.minecraft(KEY_PD_RAID_POINT));
        //int
        keyMap.put(KEY_PD_ADVANCE_POINT, NamespacedKey.minecraft(KEY_PD_ADVANCE_POINT));
        //int

        keyMap.put(KEY_ITEM_STOCK_ID, NamespacedKey.minecraft(KEY_ITEM_STOCK_ID));
        //string
        keyMap.put(KEY_ITEM_STOCK_LAST_REWARD, NamespacedKey.minecraft(KEY_ITEM_STOCK_LAST_REWARD));
        //int
        keyMap.put(KEY_ITEM_SIGNED_NAME, NamespacedKey.minecraft(KEY_ITEM_SIGNED_NAME));
        //string 標籤簽名
        keyMap.put(KEY_PD_TOTAL_SIGNIN_COUNT, NamespacedKey.minecraft(KEY_PD_TOTAL_SIGNIN_COUNT));
        //int
        keyMap.put(KEY_PD_CRIT_CHANCE, NamespacedKey.minecraft(KEY_PD_CRIT_CHANCE));
        //double
        keyMap.put(KEY_PD_CRIT_DAMAGE, NamespacedKey.minecraft(KEY_PD_CRIT_DAMAGE));
        //double

        keyMap.put(KEY_ARMOR_NAME, NamespacedKey.minecraft(KEY_ARMOR_NAME));
        //string 裝備名稱(家族名稱)
        keyMap.put(KEY_ARMOR_MANA_ADD, NamespacedKey.minecraft(KEY_ARMOR_MANA_ADD));
        //int 魔力回復的裝備標籤

        keyMap.put(KEY_ARMOR_LUCK_ADD_FIX, NamespacedKey.minecraft(KEY_ARMOR_LUCK_ADD_FIX));
        //int

        keyMap.put(KEY_ARMOR_CRIT_CHANCE_ADD, NamespacedKey.minecraft(KEY_ARMOR_CRIT_CHANCE_ADD));
        //int 魔力回復的裝備標籤
        keyMap.put(KEY_ARMOR_CRIT_DAMAGE_ADD, NamespacedKey.minecraft(KEY_ARMOR_CRIT_DAMAGE_ADD));
        //int 魔力回復的裝備標籤


    }

    public NamespacedKey getKey(String keyName) {
        return keyMap.get(keyName);
    }

    public void removeKey(String keyName) {
        keyMap.remove(keyName);
    }
}

