package me.cyperion.ntms.Class;

import me.cyperion.ntms.ItemStacks.Item.RedWand;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
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
    public final int EXPLOSION_RANGE = 14;
    public final int EXPLOSION_FRONT_BLOCK = 20;
    //（10秒 = 200 tick）
    private static final int TOTAL_TICKS = 200;
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
                    if(ticks >= TOTAL_TICKS){
                        triggerExplosion(player);
                        removeList.add(uuid);
                        bossBarMap.get(uuid).removeAll();
                        continue;
                    }
                    showCastingEffects(player,ticks);
                    ticks++;
                    executer.replace(uuid,ticks);
                }
                removeList.forEach(uuid -> {
                    bossBarMap.remove(uuid);
                    executer.remove(uuid);
                });
                removeList.clear();
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
            if(plugin.getPlayerData(player).getClassType() != ClassType.EXPLOSION) return;

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



    /**
     * 在施法過程中，每 tick 呼叫一次，根據 ticks 顯示不同特效
     * @param player  正在施法的玩家
     * @param ticks   已經過了多少 tick（0 ~ TOTAL_TICKS）
     */
    public void showCastingEffects(Player player, int ticks) {
        World world = player.getWorld();
        // 取玩家眼睛位置往前推一點作為粒子中心
        Location center = player.getEyeLocation()
                .add(player.getEyeLocation().getDirection().multiply(20));

        float progress = (float) ticks / TOTAL_TICKS;  // 0 ~ 100

        // 1. 更新 BossBar（假設你在外面已經建立並註冊過）
        BossBar bar = bossBarMap.get(player.getUniqueId());
        if (bar != null) {
            bar.setProgress(progress);
        }

        // 2. 不同進度出現不同粒子
        // 前期：紫色靈氣；中期：藍色煙霧；後期：紅色火花
        if (progress < 0.3) {
            world.spawnParticle( Particle.WITCH, center, 15, 5, 0.5, 0.5, 0.02);
        } else if (progress < 0.7) {
            world.spawnParticle(Particle.LARGE_SMOKE, center, 20, 5, 0.5, 0.5, 0.01);
        } else {
            world.spawnParticle(Particle.FLAME, center, 25, 5, 0.5, 0.5, 0.03);
        }

        // 3. 每 20 tick 播放一次蓄力音效
        if (ticks % 20 == 0) {
            world.playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f);
        }
    }

    /**
     * 施法結束時呼叫，觸發中心大爆炸 + 600 點傷害
     * @param player  觸發爆炸的玩家
     */
    public void triggerExplosion(Player player) {

        player.sendMessage(colors("&c爆炸"));
        if(player.hasPotionEffect(PotionEffectType.SLOWNESS))
            player.removePotionEffect(PotionEffectType.SLOWNESS);
        boolean success = plugin.getMana().costMana(player,400,true);
        if(!success) return;

        World world = player.getWorld();
        // 以玩家眼睛位置前方 20 格為爆炸中心
        Location center = player.getEyeLocation()
                .add(player.getEyeLocation().getDirection().multiply(EXPLOSION_FRONT_BLOCK));

        // 1. 繪製圓形爆炸粒子
        double radius = EXPLOSION_RANGE;
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 60) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Location pt = center.clone().add(x, 0, z);

            // 上方一點讓粒子不貼地
            world.spawnParticle(Particle.EXPLOSION, pt.clone().add(0, 2, 0), 1);
            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, pt.clone().add(0, 2, 0), 5, 0.2, 0.2, 0.2, 0.01);
        }

        // 播放爆炸音效
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);

        // 2. 對範圍內所有敵對生物造成傷害
        for (Entity e : world.getNearbyEntities(center, radius, radius, radius)) {
            if (e instanceof Monster) {
                ((Monster) e).damage(600.0, player);
                // 顯示傷害指示粒子
                world.spawnParticle(Particle.DAMAGE_INDICATOR, e.getLocation().add(0, 1, 0), 20);
            }
        }
    }



}
