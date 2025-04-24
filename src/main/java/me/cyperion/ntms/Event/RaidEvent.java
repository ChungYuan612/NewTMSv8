package me.cyperion.ntms.Event;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Raid;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.raid.RaidFinishEvent;
import org.bukkit.event.raid.RaidSpawnWaveEvent;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static me.cyperion.ntms.Utils.colors;

/**
 * 處理突襲相關的事件<br>
 * <p>關聯：NewTMSv8註冊</p>
 */
public class RaidEvent implements Listener {

    private NewTMSv8 plugin;

    private int RaidBouns = 4000;
    private int RaidBounsPerLevel = 400;
    public static final String META_RAID_BUFF = "raid_buff";

    public RaidEvent(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRaidStartEvent(RaidTriggerEvent event){
        //突襲開始了，我需要顯示誰在某處觸發了突襲
        Player player = event.getPlayer();
        Location location =event.getRaid().getLocation();

        Bukkit.broadcastMessage(colors(
                "&6[突襲資訊] &b"+player.getDisplayName()+
                        "&a 在座標：&d("+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+")&a 觸發了突襲!"));

    }

    @EventHandler
    public void onRaidSpawn(RaidSpawnWaveEvent e){
        //10%機率出超強怪物
        Random random = new Random();
        trySpawnMoreRaider(e.getRaid(),e.getRaid().getBadOmenLevel(),e.getRaid().getLocation());
        int i=random.nextInt(10);
        if(i < e.getRaid().getBadOmenLevel()){
            Bukkit.broadcastMessage(colors("&6[突襲資訊] &c注意!突襲出現了一波較強的敵人!"));
            for(Raider raider:e.getRaiders()){
                raider.addPotionEffect(new PotionEffect(
                        PotionEffectType.RESISTANCE,1000,2,false,true));
                raider.addPotionEffect(new PotionEffect(
                        PotionEffectType.STRENGTH, 1000, 1, false, false));

            }
            trySpawnBuffRaider(e.getRaid(),e.getRaid().getBadOmenLevel(),e.getRaid().getLocation());


        }else{
            for(Raider raider:e.getRaiders()) {
                raider.addPotionEffect(new PotionEffect(
                        PotionEffectType.STRENGTH, 1000, 0, false, false));
            }
        }

    }

    private void trySpawnMoreRaider(Raid raid, int amount, Location location) {
        for(int i = 0 ; i<amount;i++)
            //嘗試生成劫毀獸、幻術師
            try{
                Raider raider;
                raider = location.getWorld().spawn(location, Pillager.class);
                raider.addPotionEffect(
                        new PotionEffect(PotionEffectType.STRENGTH,
                                1000, 0, false, false));

                raider.setCanJoinRaid(true);
                raider.setRaid(raid);

            }catch (Exception error){
                System.out.println("[突襲突襲 ERROR]: "+error.toString());
            }
    }

    private void trySpawnBuffRaider(Raid raid,int amount, Location location){
        for(int i = 0 ; i<amount;i++)
            //嘗試生成劫毀獸、幻術師
            try{
                Raider raider;
                if(i%2==0)
                    raider = location.getWorld().spawn(location, Ravager.class);
                else
                    raider = location.getWorld().spawn(location, Illusioner.class);
                raider.addPotionEffect(
                        new PotionEffect(PotionEffectType.RESISTANCE,
                                1000,3,false,true)
                );
                raider.addPotionEffect(
                        new PotionEffect(PotionEffectType.STRENGTH,
                                1000, 2, false, false));

                raider.setMetadata(META_RAID_BUFF,new FixedMetadataValue(plugin,"true"));
                raider.setCanJoinRaid(true);
                raider.setRaid(raid);

            }catch (Exception error){
                System.out.println("[突襲突襲 ERROR]: "+error.toString());
            }
    }

    @EventHandler
    public void onRaidFinishEvent(RaidFinishEvent event){
        if(event.getRaid().getStatus().equals(Raid.RaidStatus.VICTORY)){
            int level = event.getRaid().getBadOmenLevel();
            List<Player> winner = event.getWinners();
            String heros = getAllPlayerString(winner);
            Bukkit.broadcastMessage(colors("&6[突襲資訊] &a恭喜玩家&b "
                    +heros+"&a成功打敗了 &d"+level+"級 &a的突襲！"));
            int bouns = RaidBouns + RaidBounsPerLevel * level;
            for(Player player : winner){
                plugin.getEconomy().depositPlayer(player, bouns);
                player.sendMessage(colors("&6[突襲資訊] &a你獲得了&6"+bouns+"&a元的獎金！"));
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

}
