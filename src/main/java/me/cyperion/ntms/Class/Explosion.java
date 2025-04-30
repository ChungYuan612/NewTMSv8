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
    private static final int TOTAL_TICKS = 240;
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
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.DARKNESS,60,0,true));
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


    private Particle.DustOptions options = new Particle.DustOptions(Color.RED,1);
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

        if (ticks < 40) {
            // 初始聚氣 - 紫色粒子繞圈
            drawCircleParticles(world, center, Particle.WITCH, 2 + ticks * 1.7, 0.3, 16);
            if (ticks % 20 == 0)
                world.playSound(center, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.8f, 1.2f);
        } else if (ticks < 140) {
            // 中期：紅藍粒子吸入，漸強火焰
            drawConvergingParticles(world, center,Particle.DUST , 30, 6,options);
            drawConvergingParticles(world, center, Particle.PORTAL, 30, 4);
            drawCircleParticles(world, center,Particle.DUST , 30, 6,16,options);
            world.spawnParticle(Particle.FLAME, center, 4, 2, 2, 2, 0.1);
            if (ticks % 20 == 0)
                world.playSound(center, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);
        } else if (ticks < 220) {
            // 後期震動 - 光線抖動與火花
            drawShakingParticles(world, center, Particle.END_ROD, 4.2, 8);
            world.spawnParticle(Particle.CRIT, center, 10, 0.3, 0.3, 0.3, 0.05);
            if (ticks % 10 == 0)
                world.playSound(center, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.2f);
        }
        if(ticks == 200){
            List<Entity> entities = player.getNearbyEntities(100,50,100);
            for(Entity entity : entities){
                if(entity instanceof Player other){
                    other.addPotionEffect(new PotionEffect(
                            PotionEffectType.DARKNESS,20,0,true));
                }
            }
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.DARKNESS,20,0,true));
        }
        //for(int i = 1; i< ticks;i+=50){
            //spawnFilledCircle(center.add(0,((double) ticks /i)*2,0), 12+(ticks/i));
        //}
        spawnFilledCircle(player.getLocation(),3);
        world.spawnParticle(Particle.BUBBLE,player.getLocation(),5,0.1,0.1,0.1,1);


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

        // 煙霧 + 火花 + 爆炸粒子
        for(int y = -3; y <= 3; y++)
            for (int i = 0; i < 360; i += 6) {
                double rad = Math.toRadians(i);
                double x = Math.cos(rad) * 12-y;
                double z = Math.sin(rad) * 12-y;
                Location point = center.clone().add(x, y, z);
                world.spawnParticle(Particle.EXPLOSION, point, 1);
                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, point, 2, 0.6, 0.6, 0.6, 0.01);
                world.spawnParticle(Particle.FLAME, point, 7, 0.5, 0.5, 0.5, 0.02);
            }

        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1.9f, 0.65f);
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2f, 0.5f);

        world.spawnParticle(Particle.FLAME,center,1000,8,8,8,5);

        // 2. 對範圍內所有敵對生物造成傷害
        for (Entity e : world.getNearbyEntities(center, EXPLOSION_RANGE, EXPLOSION_RANGE, EXPLOSION_RANGE)) {
            if (e instanceof Monster) {
                ((Monster) e).damage(600.0, player);
                // 顯示傷害指示粒子
                world.spawnParticle(Particle.DAMAGE_INDICATOR, e.getLocation().add(0, 1, 0), 20);
            }
        }
    }

    /**
     * 畫圓形的粒子效果
     */
    public void drawCircleParticles(
            World world, Location center, Particle particle,
            double radius, double y, int count){
        drawCircleParticles(world, center, particle, radius, y, count, null);
    }
    public void drawCircleParticles(
            World world, Location center, Particle particle,
            double radius, double y, int count, Particle.DustOptions options) {
        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            if(options == null)
                world.spawnParticle(particle, center.clone(), 2, x, y, z, 0);
            else
                world.spawnParticle(particle, center.clone(), 2, x, y, z, 0.5f, options,true);
        }
    }
    /**
     * 畫震動的粒子效果
     */
    public void drawShakingParticles(World world, Location center, Particle particle, double spread, int count) {
        for (int i = 0; i < count; i++) {
            double x = (Math.random() - 0.5) * spread;
            double y = (Math.random() - 0.5) * spread;
            double z = (Math.random() - 0.5) * spread;
            world.spawnParticle(particle, center.clone().add(x, y, z), 5);
        }
    }
    /**
     * 畫吸入的粒子效果
     */
    public void drawConvergingParticles(World world, Location center, Particle particle, double range, int count) {
        drawConvergingParticles(world, center, particle, range, count, null);
    }

    public void drawConvergingParticles(World world,
                                        Location center,
                                        Particle particle,
                                        double range,
                                        int count,
                                        Particle.DustOptions options){
        for (int i = 0; i < count; i++) {
            double x = (Math.random() - 0.5) * range;
            double y = (Math.random() - 0.5) * range;
            double z = (Math.random() - 0.5) * range;
            Location start = center.clone().add(x, y, z);
            if(options != null)
                world.spawnParticle(particle, start, 10, -x / 10, -y / 10, -z / 10, 2,options);
            else
                world.spawnParticle(particle, start, 10, -x / 10, -y / 10, -z / 10, 2);
        }
    }

    public void spawnFilledCircle(Location center, int range) {
        World world = center.getWorld();
        if (world == null) return;

        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (x * x + z * z <= range * range) {
                    Location loc = center.clone().add(x, 0, z);
                    world.spawnParticle(Particle.DUST, loc, 1, 0.5, 0, 0.15, 0.05,options,true);
                }
            }
        }
    }




}
