package me.cyperion.ntms;

import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Map;

/**
 * 這個類別專門放所有的NameSpaceKey
 * 使用字串常數可以快速取得NameSpaceKey
 */
public class NSKeyRepo {

    private Map<String, NamespacedKey> keyMap;
    public final String KEY_PD_ADVANCE_POINT = "player_data_advance_point";
    public final String KEY_PD_LUCK = "player_data_luck";
    public final String KEY_PD_MANA = "player_data_mana";
    public final String KEY_PD_MAX_MANA = "player_data_max_mana";
    public final String KEY_PD_MANA_REG = "player_data_mana_reg";
    public final String KEY_PD_CLASS_TYPE = "player_data_class_type";
    public final String KEY_PD_PERK_FIRST = "player_data_perk_first";
    public final String KEY_PD_PERK_SECOND = "player_data_perk_second";
    public final String KEY_PD_PERK_THIRD = "player_data_perk_third";
    public final String KEY_PD_ALLOW_OVER_MANA = "player_data_allow_over_mana";
    public final String KEY_PD_RAID_POINT = "player_data_raid_point";
    public final String KEY_PD_SHOW_MANA = "player_data_show_mana";
    public final String KEY_PD_TOTAL_SIGNIN_COUNT = "player_data_total_signin_count";

    public final String KEY_ITEM_STOCK_ID = "item_stock_id";
    public final String KEY_ITEM_STOCK_LAST_REWARD = "item_stock_last_reward";

    public final String KEY_ARMOR_MANA_ADD = "key_armor_mana_add";


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
        keyMap.put(KEY_PD_TOTAL_SIGNIN_COUNT, NamespacedKey.minecraft(KEY_PD_TOTAL_SIGNIN_COUNT));
        //int

        keyMap.put(KEY_ARMOR_MANA_ADD, NamespacedKey.minecraft(KEY_ARMOR_MANA_ADD));
        //int 魔力回復的裝備標籤


    }

    public NamespacedKey getKey(String keyName) {
        return keyMap.get(keyName);
    }

    public void removeKey(String keyName) {
        keyMap.remove(keyName);
    }
}

