package me.cyperion.ntms;

import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * 負責處理所有modifier的地方 執行續放在跟Mana同地方呼叫
 */
public class ModifierMain {


    private NewTMSv8 plugin;


    public static HashMap<UUID, List<Modifier>> modifiers = new HashMap<>();

    public ModifierMain(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public void run () {
        for(Player player : Bukkit.getOnlinePlayers()){
            //MANA MODIFIER BEGIN-------------------------
            List<Modifier> list = modifiers.get(player.getUniqueId());
            if(list == null) {
                modifiers.put(player.getUniqueId(),new ArrayList<>());
                continue;
            }
            //注意! 我這裡寫得很暴力，直接個別作處理而已 之後需要優化 TODO
            double addon=0D,multiply = 1.0;
            double regan_addon=0D,regan_multiply = 1.0;
            double luck_addon=0D,luck_multiply = 1.0;
            double cc_addon=0D,cc_multiply = 1.0;
            double cd_addon=0D,cd_multiply = 1.0;
            for(Modifier modifier : list) {
                //System.out.println(player.getName()+": "+modifier.getNameSpaceKey()+": "+modifier.getValue()+": "+modifier.getID());

                if(modifier.getType().equals(ModifierType.ADD)){
                    if(modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_MAX_MANA))
                        addon += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_MANA_REG))
                        regan_addon += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_LUCK))
                        luck_addon += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_CRIT_CHANCE))
                        cc_addon += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_CRIT_DAMAGE))
                        cd_addon += modifier.getValue();
                }else if(modifier.getType().equals(ModifierType.MULTIPLY)){
                    if(modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_MAX_MANA))
                        multiply += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_MANA_REG))
                        regan_multiply += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_LUCK))
                        luck_multiply += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_CRIT_CHANCE))
                        cc_multiply += modifier.getValue();
                    else if (modifier.getNameSpaceKey().equals(NSKeyRepo.KEY_PD_CRIT_DAMAGE))
                        cd_multiply += modifier.getValue();
                }
            }
            double maxMana = (Mana.defaultMaxMana + addon) * multiply;
            double manaReg = (Mana.defaultManaRegan + regan_addon) * regan_multiply;
            double luck = (PlayerData.DEFAULT_LUCK + luck_addon) * luck_multiply;
            double cc = (PlayerData.DEFAULT_CRIT_CHANCE + cc_addon) * cc_multiply;
            double cd = (PlayerData.DEFAULT_CRIT_DAMAGE + cd_addon) * cd_multiply;
            plugin.getPlayerData(player).setMaxMana(maxMana);
            plugin.getPlayerData(player).setManaReg(manaReg);
            plugin.getPlayerData(player).setLuck(luck);
            plugin.getPlayerData(player).setCritChance(cc);
            plugin.getPlayerData(player).setCritDamage(cd);
            //MODIFIER END-------------------------
        }
    }


    /**
     * 檢查該玩家是否有該ID的Modifier
     * @param player
     * @param ID
     */
    public boolean hasModifier(Player player, String ID){
        if(!modifiers.containsKey(player.getUniqueId()))
            return false;
        for(Modifier modifier : modifiers.get(player.getUniqueId())) {
            if(modifier.getID().equals(ID))
                return true;
        }
        return false;
    }

    /**
     * 新增Player的Modifier
     * @param player
     * @param modifier
     */
    public void addModifier(Player player, Modifier modifier){
        if(!modifiers.containsKey(player.getUniqueId()))
            modifiers.put(player.getUniqueId(),new ArrayList<>());
        modifiers.get(player.getUniqueId()).add(modifier);
    }

    /**
     * 刪除Player的Modifier
     * @param player
     * @param ID
     * @return
     */
    public boolean removeModifier(Player player, String ID){
        if(!modifiers.containsKey(player.getUniqueId()))
            return true;
        for(Modifier modifier : modifiers.get(player.getUniqueId())){
            if(modifier.getID().equals(ID)){
                modifiers.get(player.getUniqueId()).remove(modifier);
                return true;
            }
        }
        return false;
    }

    public static HashMap<UUID, List<Modifier>> getModifiers() {
        return modifiers;
    }

    public void clearModifier(){
        for(UUID player : modifiers.keySet()){
            List<Modifier> list = modifiers.get(player);
            list.clear();
        }
    }
}
