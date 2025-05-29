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

    private NewTMSv8 plugin;
    /**
     * maxMana 最大魔力<br>
     * mana 目前魔力<br>
     * manaReg 每秒增加的魔力
     */
    private double maxMana,mana,manaReg;

    public static final double DEFAULT_MANA_REG=1.0;
    public static final double DEFAULT_LUCK=0.0;

    //職業，沒有的話為None
    private ClassType classType;

    //透支魔力、顯示魔力
    private boolean allowOverMana,showMana;

    //從0沒有升級~ (目前棄用)
    private int perkFirst,perkSecond,perkThird;

    private int advancePoint,raidPoint,signinCount;



    //幸運等級
    private double luck;

    private UUID uuid;

    /**
     * 取得玩家的特殊資料(Mana之類的)，如果沒有則幫他加一個新數值
     * @param player 玩家
     * @return 玩家特殊資料
     */
    public PlayerData(NewTMSv8 plugin, Player player){
        this.plugin = plugin;
        this.uuid = player.getUniqueId();
        PersistentDataContainer container = player.getPersistentDataContainer();
        NSKeyRepo repo = plugin.getNsKeyRepo();

        //確認玩家有沒有NSKey，沒有就幫他裝新的
        if( !container.has(
                repo.getKey(NSKeyRepo.KEY_PD_MAX_MANA))
        ){
            //使用數值MANA有無存在來判斷這個玩家是不是有特殊數值，或者是剛開始加入的
            container.set(repo.getKey(NSKeyRepo.KEY_PD_MAX_MANA),
                    PersistentDataType.DOUBLE,Mana.defaultMaxMana);
            container.set(repo.getKey(NSKeyRepo.KEY_PD_MANA_REG),
                    PersistentDataType.DOUBLE,Mana.defaultManaRegan);
            container.set(repo.getKey(NSKeyRepo.KEY_PD_MANA),
                    PersistentDataType.DOUBLE,0.0);
        }

        this.allowOverMana = checkAndSetData(repo.getKey(repo.KEY_PD_ALLOW_OVER_MANA),false);

        this.maxMana = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_MAX_MANA),Mana.defaultMaxMana);
        this.manaReg = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_MANA_REG),Mana.defaultManaRegan);
        this.mana = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_MANA),0.0);
        this.showMana = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_SHOW_MANA),true);

        this.classType = ClassType.valueOf(checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_CLASS_TYPE),ClassType.NONE.toString()));

        this.perkFirst = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_PERK_FIRST),0);
        this.perkSecond = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_PERK_SECOND),0);
        this.perkThird = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_PERK_THIRD),0);

        this.advancePoint = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_ADVANCE_POINT),0);
        this.luck = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_LUCK),0.0);
        this.signinCount = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_TOTAL_SIGNIN_COUNT),0);

        this.raidPoint = checkAndSetData(repo.getKey(NSKeyRepo.KEY_PD_RAID_POINT),0);
        //---更新區---
        String updateKey = NSKeyRepo.KEY_PD_RAID_POINT;//這個做為之後更新時的地方
        if( container.has(
                repo.getKey(updateKey))
        ){
            //幫現有玩家更新上去

            //container.set(repo.getKey(repo.KEY_PD_MAX_MANA),
            //       PersistentDataType.INTEGER,50);
        }
        //---更新結束---

    }

    /* 初始化玩家資料 泛型實作 */
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

    private boolean checkAndSetData(NamespacedKey key, boolean v){
        Player player = Bukkit.getPlayer(uuid);
        PersistentDataContainer container = player.getPersistentDataContainer();
        if(!container.has(key)){
            container.set(key,PersistentDataType.BOOLEAN,v);
            return v;
        }
        return container.get(key,PersistentDataType.BOOLEAN);
    }

    /* 初始化玩家資料 結束 */

    /*成就點數*/

    public int getAdvancePoint() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(repo.KEY_PD_ADVANCE_POINT),PersistentDataType.INTEGER
        );
    }

    public void setAdvancePoint(int advancePoint) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(repo.KEY_PD_ADVANCE_POINT),PersistentDataType.INTEGER,advancePoint
        );
    }

    public void addAdvancePoint(int advancePoint) {
        int oldAdvancePoint = getAdvancePoint();
        setAdvancePoint(oldAdvancePoint + advancePoint);
    }

    /* Perk (目前棄用)*/

    @Deprecated
    public void setPerkFirst(int perkFirst) {
        this.perkFirst = perkFirst;
    }

    @Deprecated
    public void setPerkSecond(int perkSecond) {
        this.perkSecond = perkSecond;
    }

    @Deprecated
    public void setPerkThird(int perkThird) {
        this.perkThird = perkThird;
    }

    @Deprecated
    public int getPerkFirst() {
        return perkFirst;
    }

    @Deprecated
    public int getPerkSecond() {
        return perkSecond;
    }

    @Deprecated
    public int getPerkThird() {
        return perkThird;
    }

    /** <h1>魔力方面的東西都寫在Mana.java內 這裡只負責資料存取</h1> **/
    /* 魔力上限數值 */

    public double getMaxMana() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_MAX_MANA),PersistentDataType.DOUBLE
        );
    }

    public void setMaxMana(double maxMana) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_MAX_MANA),PersistentDataType.DOUBLE,maxMana
        );
    }

    private void addMaxMana(double maxMana) {
        double oldMaxMana = getMaxMana();
        setMaxMana(oldMaxMana + maxMana);
    }

    /* 魔力數值 */

    public double getMana() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_MANA),PersistentDataType.DOUBLE
        );
    }

    public void setMana(double mana) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_MANA),PersistentDataType.DOUBLE,mana
        );
    }

    /* 魔力恢復速度數值 */

    public double getManaReg() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_MANA_REG),PersistentDataType.DOUBLE
        );
    }

    public void setManaReg(double manaReg) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_MANA_REG),PersistentDataType.DOUBLE,manaReg
        );
    }

    /* 是否允許透支魔力 */
    public boolean isAllowOverMana() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_ALLOW_OVER_MANA),PersistentDataType.BOOLEAN
        );
    }

    public void setAllowOverMana(boolean allowOverMana) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_ALLOW_OVER_MANA),PersistentDataType.BOOLEAN,allowOverMana
        );
    }

    /* 職業類別 */

    public ClassType getClassType() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return ClassType.valueOf(
                getPlayerPesistentData().get(
                        repo.getKey(NSKeyRepo.KEY_PD_CLASS_TYPE),PersistentDataType.STRING
                )
        );
    }

    public void setClassType(ClassType classType) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_CLASS_TYPE),PersistentDataType.STRING,classType.name()
        );
    }

    /* 是否顯示魔力在物品來上方 */

    public boolean getShowManaOnActionbar() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_SHOW_MANA),PersistentDataType.BOOLEAN
        );
    }

    public void setShowManaOnActionbar(boolean showMana) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_SHOW_MANA),PersistentDataType.BOOLEAN, showMana
        );
    }

    /* 突襲計算次數 */

    public int getRaidPoint() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_RAID_POINT),PersistentDataType.INTEGER
        );
    }

    public void setRaidPoint(int integer) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_RAID_POINT),PersistentDataType.INTEGER, integer
        );
    }

    public void addRaidPoint(int integer) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_RAID_POINT),PersistentDataType.INTEGER,getRaidPoint() + integer
        );
    }

    /* 幸運點 */

    public double getLuck() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_LUCK),PersistentDataType.DOUBLE
        );
    }

    public void setLuck(double v) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_LUCK),PersistentDataType.DOUBLE, v
        );
    }

    public void addLuck(double v) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_LUCK),PersistentDataType.DOUBLE,getLuck() + v
        );
    }

    /* 累計簽到次數 */

    public int getSignInCount() {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        return getPlayerPesistentData().get(
                repo.getKey(NSKeyRepo.KEY_PD_TOTAL_SIGNIN_COUNT),PersistentDataType.INTEGER
        );
    }

    public void setSignInCount(int v) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_TOTAL_SIGNIN_COUNT),PersistentDataType.INTEGER, v
        );
    }

    public void addSignInCount(int v) {
        NSKeyRepo repo = this.plugin.getNsKeyRepo();
        this.getPlayerPesistentData().set(
                repo.getKey(NSKeyRepo.KEY_PD_TOTAL_SIGNIN_COUNT),PersistentDataType.INTEGER,getSignInCount() + v
        );
    }

    /* ----- */


    public UUID getUuid() {
        return uuid;
    }

    public Player getPlayer(){
        return Bukkit.getPlayer(uuid);
    }

    private PersistentDataContainer getPlayerPesistentData(){
        return getPlayer().getPersistentDataContainer();
    }

}
