package me.cyperion.ntms.Player;

import me.cyperion.ntms.Class.ClassType;
import me.cyperion.ntms.Mana;
import me.cyperion.ntms.NSKeyRepo;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class PlayerData {
    /**
     * maxMana 最大魔力
     * mana 目前魔力
     * manaReg 每秒增加的魔力
     */
    private double maxMana,mana,manaReg;

    //職業，沒有的話為None
    private ClassType classType;

    //透支魔力
    private boolean allowOverMana;

    //從0沒有升級~
    private int perkFirst,perkSecond,perkThird;


    private int advancePoint;

    private UUID uuid;

    /**
     * 取得玩家的特殊資料(Mana之類的)，如果沒有則幫他加一個新數值
     * @param player 玩家
     * @return 玩家特殊資料
     */
    public PlayerData(NewTMSv8 plugin, Player player){
        this.uuid = player.getUniqueId();
        PersistentDataContainer container = player.getPersistentDataContainer();
        NSKeyRepo repo = plugin.getNsKeyRepo();

        //確認玩家有沒有NSKey，沒有就幫他裝新的
        if( !container.has(
                repo.getKey(repo.KEY_PD_MAX_MANA))
        ){
            //使用數值MANA有無存在來判斷這個玩家是不是有特殊數值，或者是剛開始加入的
            container.set(repo.getKey(repo.KEY_PD_MAX_MANA),
                    PersistentDataType.DOUBLE,Mana.defaultMaxMana);
            container.set(repo.getKey(repo.KEY_PD_MANA_REG),
                    PersistentDataType.DOUBLE,1.0);
            container.set(repo.getKey(repo.KEY_PD_MANA),
                    PersistentDataType.DOUBLE,0.0);
        }

        this.maxMana = checkAndSetData(repo.getKey(repo.KEY_PD_MAX_MANA),Mana.defaultMaxMana);
        this.manaReg = checkAndSetData(repo.getKey(repo.KEY_PD_MANA_REG),1.0);
        this.mana = checkAndSetData(repo.getKey(repo.KEY_PD_MANA),0.0);

        this.classType = ClassType.valueOf(checkAndSetData(repo.getKey(repo.KEY_PD_CLASS_TYPE),ClassType.NONE.toString()));

        this.perkFirst = checkAndSetData(repo.getKey(repo.KEY_PD_PERK_FIRST),0);
        this.perkSecond = checkAndSetData(repo.getKey(repo.KEY_PD_PERK_SECOND),0);
        this.perkThird = checkAndSetData(repo.getKey(repo.KEY_PD_PERK_THIRD),0);

        this.advancePoint = checkAndSetData(repo.getKey(repo.KEY_PD_ADVANCE_POINT),0);
        //---更新區---
        String updateKey = "";//這個做為之後更新時的地方
        if( container.has(
                repo.getKey(updateKey))
        ){
            //幫現有玩家更新上去

            //container.set(repo.getKey(repo.KEY_PD_MAX_MANA),
            //       PersistentDataType.INTEGER,50);
        }
        //---更新結束---

    }

    public void savePlayerData(){
        //TODO

    }

    private double checkAndSetData(NamespacedKey key, double value){
        Player player = Bukkit.getPlayer(uuid);
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(key)){
            container.set(key,PersistentDataType.DOUBLE,value);
            return value;
        }
        return container.get(key,PersistentDataType.DOUBLE);
    }

    private String checkAndSetData(NamespacedKey key, String v){
        Player player = Bukkit.getPlayer(uuid);
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(key)){
            container.set(key,PersistentDataType.STRING,v);
            return v;
        }
        return container.get(key,PersistentDataType.STRING);
    }
    private int checkAndSetData(NamespacedKey key, int v){
        Player player = Bukkit.getPlayer(uuid);
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(key)){
            container.set(key,PersistentDataType.INTEGER,v);
            return v;
        }
        return container.get(key,PersistentDataType.INTEGER);
    }

    public int getAdvancePoint() {
        return advancePoint;
    }

    public void setAdvancePoint(int advancePoint) {
        this.advancePoint = advancePoint;
    }

    public void setPerkFirst(int perkFirst) {
        this.perkFirst = perkFirst;
    }

    public void setPerkSecond(int perkSecond) {
        this.perkSecond = perkSecond;
    }

    public void setPerkThird(int perkThird) {
        this.perkThird = perkThird;
    }

    public int getPerkFirst() {
        return perkFirst;
    }

    public int getPerkSecond() {
        return perkSecond;
    }

    public int getPerkThird() {
        return perkThird;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public void setMaxMana(double maxMana) {
        this.maxMana = maxMana;
    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public double getManaReg() {
        return manaReg;
    }

    public void setManaReg(double manaReg) {
        this.manaReg = manaReg;
    }

    public boolean isAllowOverMana() {
        return allowOverMana;
    }

    public void setAllowOverMana(boolean allowOverMana) {
        this.allowOverMana = allowOverMana;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public UUID getUuid() {
        return uuid;
    }
}
