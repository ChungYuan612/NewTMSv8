package me.cyperion.ntms.Class;

import me.cyperion.ntms.ItemStacks.Item.RedWand;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

import static me.cyperion.ntms.Utils.colors;

public class Explosion extends Class implements Listener {

    //施放爆裂魔法的人
    private final Map<UUID,Integer> executer = new HashMap<>();
    private BukkitRunnable runnable;
    private final Map<UUID,BossBar> bossBarMap = new HashMap<>();
    public Explosion(NewTMSv8 plugin) {
        super(plugin);
        runnable = new BukkitRunnable(){
            private final Set<UUID> executerKeySet = executer.keySet();
            private final List<UUID> removeList = new ArrayList<>();
            @Override
            public void run() {
                for (UUID uuid :executerKeySet){
                    int ticks = executer.get(uuid);
                    Player player = Bukkit.getPlayer(uuid);

                    if(player == null || !player.isOnline() || !player.isSneaking()){
                        player.sendMessage(colors("&c技能施放失敗"));
                        removeList.add(uuid);
                        bossBarMap.get(uuid).removeAll();
                        continue;
                    }
                    if(ticks % 20 == 0){
                        player.playSound(
                                player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT,1f,1);
                    }
                    if(ticks > 20*10){
                        Explosion(player);
                    }
                    bossBarMap.get(uuid).setProgress((int)((double) ticks /200*100));
                    ticks++;
                    executer.replace(uuid,ticks);
                }
                removeList.forEach(uuid -> {
                    bossBarMap.remove(uuid);
                    removeList.remove(uuid);
                    executer.remove(uuid);
                });
            }
        };
        runnable.runTaskTimer(plugin,0L,1L);
    }

    @Override
    public ClassType getClassType() {
        return ClassType.EXPLOSION;
    }

    @Override
    public String getName() {
        return colors("&cエクスプロージョン &4Explosion");
    }

    @EventHandler
    public void onPlayerExplosionReady(PlayerInteractEvent event){
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            Player player = event.getPlayer();
            ItemStack wand  = new RedWand(plugin).toItemStack();//TODO 紅魔法杖
            ItemStack playerWand = player.getInventory().getItemInMainHand();

            if(!playerWand.hasItemMeta()) return;
            if(executer.containsKey(player.getUniqueId())) return;
            if(!playerWand.getItemMeta().hasCustomModelData()) return;
            if(playerWand.getItemMeta().getCustomModelData() != wand.getItemMeta().getCustomModelData()) return;
            if(plugin.getPlayerData(player).getMana() <= 0) return;
            if(!player.isSneaking()) return;
            executer.put(player.getUniqueId(),0);
            bossBarMap.put(player.getUniqueId(),createBossBar(player));
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SLOWNESS,9000,3,true));
        }
    }

    private BossBar createBossBar(Player player) {
        BossBar bossBar = Bukkit.createBossBar(
                colors("&6&l爆裂魔法蓄力中.."),
                BarColor.RED,
                BarStyle.SEGMENTED_10,
                BarFlag.DARKEN_SKY
        );
        bossBar.setProgress(0);
        bossBar.setVisible(true);
        bossBar.addPlayer(player);
        return bossBar;
    }

    private void Explosion(Player player){
        player.sendMessage(colors("&c爆炸"));
        if(player.hasPotionEffect(PotionEffectType.SLOWNESS))
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        boolean success = plugin.getMana().costMana(player,400,true);
        if(!success) return;

    }


}
