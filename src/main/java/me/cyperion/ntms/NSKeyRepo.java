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
    public final String KEY_PD_MANA = "player_data_mana";
    public final String KEY_PD_MAX_MANA = "player_data_max_mana";
    public final String KEY_PD_MANA_REG = "player_data_mana_reg";
    public NSKeyRepo() {
        this.keyMap = new HashMap<>();

        keyMap.put(KEY_PD_MANA, NamespacedKey.minecraft(KEY_PD_MANA));
        //double
        keyMap.put(KEY_PD_MAX_MANA, NamespacedKey.minecraft(KEY_PD_MANA));
        //double
        keyMap.put(KEY_PD_MANA_REG, NamespacedKey.minecraft(KEY_PD_MANA_REG));
        //double

    }

    public NamespacedKey getKey(String keyName) {
        return keyMap.get(keyName);
    }


    public void removeKey(String keyName) {
        keyMap.remove(keyName);
    }
}

