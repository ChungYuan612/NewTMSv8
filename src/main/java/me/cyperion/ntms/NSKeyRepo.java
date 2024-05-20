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
    public final String KEY_PD_MANA = "player_data_mana";
    public final String KEY_PD_MAX_MANA = "player_data_max_mana";
    public final String KEY_PD_MANA_REG = "player_data_mana_reg";
    public final String KEY_PD_CLASS_TYPE = "player_data_class_type";
    public final String KEY_PD_PERK_FIRST = "player_data_perk_first";
    public final String KEY_PD_PERK_SECOND = "player_data_perk_second";
    public final String KEY_PD_PERK_THIRD = "player_data_perk_third";

    public NSKeyRepo() {
        this.keyMap = new HashMap<>();

        keyMap.put(KEY_PD_MANA, NamespacedKey.minecraft(KEY_PD_MANA));
        //double

        keyMap.put(KEY_PD_MAX_MANA, NamespacedKey.minecraft(KEY_PD_MANA));
        //double

        keyMap.put(KEY_PD_MANA_REG, NamespacedKey.minecraft(KEY_PD_MANA_REG));
        //double

        keyMap.put(KEY_PD_CLASS_TYPE, NamespacedKey.minecraft(KEY_PD_CLASS_TYPE));
        //string

        keyMap.put(KEY_PD_PERK_FIRST, NamespacedKey.minecraft(KEY_PD_CLASS_TYPE));
        //string
        keyMap.put(KEY_PD_PERK_SECOND, NamespacedKey.minecraft(KEY_PD_CLASS_TYPE));
        //string
        keyMap.put(KEY_PD_PERK_THIRD, NamespacedKey.minecraft(KEY_PD_CLASS_TYPE));
        //string

    }

    public NamespacedKey getKey(String keyName) {
        return keyMap.get(keyName);
    }

    public void removeKey(String keyName) {
        keyMap.remove(keyName);
    }
}

