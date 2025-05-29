package me.cyperion.ntms.Event;

import me.cyperion.ntms.ItemStacks.Item.Emerald_Coins;
import me.cyperion.ntms.Monster.RewardItem;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Raid;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

import static me.cyperion.ntms.Utils.colors;

/**
 * 處理突襲相關的事件<br>
 * <p>關聯：NewTMSv8註冊</p>
 * <p>關聯：Monster/MonsterRegister有掉落物相關</p>
 */
public class RaidEvent implements Listener {

    private NewTMSv8 plugin;

    private int RaidBouns = 4000;
    private int RaidBounsPerLevel = 400;
    public static final String META_RAID_BUFF = "raid_buff";
    private Random random = new Random();
    private ItemStack quickCrossBow = null;
    private ItemStack diamondAxe = null;

    public RaidEvent(NewTMSv8 plugin) {
        this.plugin = plugin;
        quickCrossBow = getQuickCrossBow();
        diamondAxe = getBuffDiamondAxe();
    }



    @EventHandler
    public void onRaidStartEvent(RaidTriggerEvent event){
        //突襲開始了，我需要顯示誰在某處觸發了突襲
        Player player = event.getPlayer();
        Location location =event.getRaid().getLocation();

        Bukkit.broadcastMessage(colors(
                "&6[突襲資訊] &b"+player.getName()+
                        "&a 在座標：&d("+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+")&a 觸發了突襲!"));

    }

    @EventHandler
    public void onRaidSpawn(RaidSpawnWaveEvent e){

        for(Raider raider:e.getRaiders()){
            if(raider instanceof Pillager p){
                if(random.nextInt(10) >= 5){
                    p.getEquipment().setItemInMainHand(quickCrossBow);
                    p.getEquipment().setItemInMainHandDropChance(0f);
                }
            }else if(raider instanceof Vindicator v){
                if(random.nextInt(10) >= 5){
                    v.getEquipment().setItemInMainHand(diamondAxe);
                    v.getEquipment().setItemInMainHandDropChance(0f);
                }
            }else if(raider instanceof Evoker evoker){
                if(random.nextInt(10) >= 3){
                    evoker.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 9000, 2, false, false));
                }
            }
            raider.addPotionEffect(new PotionEffect(
                    PotionEffectType.STRENGTH, 9000, 0, false, false));
            raider.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION, 9000, 0, false, false));
        }
        //10%*突襲等級 機率出超強怪物
        int i=random.nextInt(10);
        if(i < e.getRaid().getBadOmenLevel()){
            Bukkit.broadcastMessage(colors("&6[突襲資訊] &c注意!突襲出現了一波較強的敵人!"));
            for(Raider raider:e.getRaiders()){
                if(raider instanceof Ravager){
                    //if(random.nextInt(10) >= 5){ 全部都允許掉落青金石了
                        //允許掉落青金石
                        raider.setMetadata(META_RAID_BUFF,new FixedMetadataValue(plugin,"true"));
                    //}
                }
                raider.addPotionEffect(new PotionEffect(
                        PotionEffectType.RESISTANCE,9000,2,false,true));
                raider.addPotionEffect(new PotionEffect(
                        PotionEffectType.STRENGTH, 9000, 1, false, false));

            }
            trySpawnBuffRaider(e.getRaid(),e.getRaid().getBadOmenLevel());


        }

    }

    @Deprecated
    private void trySpawnMoreRaider(Raid raid, int amount) {
        for(int i = 0 ; i<amount;i++)
            //嘗試生成劫毀獸、幻術師
            try{

                Location location = raid.getRaiders().get(random.nextInt(raid.getRaiders().size())).getLocation();

                Raider raider;
                if(random.nextInt(10) >= 2.5)
                    raider = location.getWorld().spawn(location, Pillager.class);
                else
                    raider = location.getWorld().spawn(location, Ravager.class);
                raider.addPotionEffect(
                        new PotionEffect(PotionEffectType.STRENGTH,
                                1000, 0, false, false));

                raider.setCanJoinRaid(true);
                raider.setRaid(raid);
                raider.setWave(raid.getRaiders().getFirst().getWave());


            }catch (Exception error){
                System.out.println("[突襲突襲 ERROR]: "+error.toString());
            }
    }

    private void trySpawnBuffRaider(Raid raid,int amount){
        for(int i = 0 ; i<amount;i++)
            //嘗試生成劫毀獸、幻術師
            try{
                Location location = raid.getRaiders().get(random.nextInt(raid.getRaiders().size())).getLocation();
                Raider raider;
                if(i%2==0)
                    raider = location.getWorld().spawn(location, Ravager.class);
                else
                    raider = location.getWorld().spawn(location, Illusioner.class);
                raider.addPotionEffect(
                        new PotionEffect(PotionEffectType.RESISTANCE,
                                9000,2,false,true)
                );
                raider.addPotionEffect(
                        new PotionEffect(PotionEffectType.STRENGTH,
                                9000, 2, false, false));

                raider.setMetadata(META_RAID_BUFF,new FixedMetadataValue(plugin,"true"));
                raider.setCanJoinRaid(true);
                raider.setRaid(raid);
                raider.setWave(raid.getRaiders().getFirst().getWave());
            }catch (Exception error){
                System.out.println("[突襲突襲 ERROR]: "+error.toString());
            }
    }

    private final Emerald_Coins emerald = new Emerald_Coins();

    @EventHandler
    public void onRaidFinishEvent(RaidFinishEvent event){
        if(event.getRaid().getStatus().equals(Raid.RaidStatus.VICTORY)){
            int level = event.getRaid().getBadOmenLevel();
            List<Player> winner = event.getWinners();
            String heros = getAllPlayerString(winner);
            Bukkit.broadcastMessage(colors("&6[突襲資訊] &a恭喜玩家&b "
                    +heros+"&a成功打敗了 &d"+level+"級 &a的突襲！"));
            int bouns = RaidBouns + RaidBounsPerLevel * level;

            RewardItem rewardItem = new RewardItem(plugin,emerald.toItemStack(), 1, 1, 3 * level);
            for(Player player : winner){
                rewardItem.tryDropLoot(player);
                int p = bouns + (random.nextInt(level*100)-level*50);
                plugin.getEconomy().depositPlayer(player, p);
                player.sendMessage(colors("&6[突襲資訊] &a你獲得了&6"+p+"&a元的獎金！"));
                plugin.getPlayerData(player).addRaidPoint(1);
            }
        }
    }

    private String getAllPlayerString(List<Player> players) {
        StringJoiner herosName = new StringJoiner("&r&f,");
        for (Player p: players) {
            herosName.add("&r&b"+p.getName());
        }
        return herosName.toString();
    }

    private ItemStack getQuickCrossBow(){
        ItemStack item = new ItemStack(Material.CROSSBOW);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.QUICK_CHARGE,4,true);
        meta.addEnchant(Enchantment.FLAME,0,true);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getBuffDiamondAxe() {
        ItemStack item = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.SHARPNESS,4,true);
        item.setItemMeta(meta);
        return item;
    }

}
