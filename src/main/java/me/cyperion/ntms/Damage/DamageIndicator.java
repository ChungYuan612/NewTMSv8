package me.cyperion.ntms.Damage;

import com.comphenix.protocol.events.PacketContainer;
import com.loohp.holomobhealth.HoloMobHealth;
import com.loohp.holomobhealth.libs.net.kyori.adventure.text.Component;
import com.loohp.holomobhealth.nms.NMS;
import com.loohp.holomobhealth.registries.DisplayTextCacher;
import com.loohp.holomobhealth.utils.*;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.cyperion.ntms.Utils.colors;

/**
 * 從HoloMobHealth複製過來的，爆擊也是在這裡處理
 */
public class DamageIndicator implements Listener {

    private final NewTMSv8 plugin;

    private static final Random RANDOM = new Random();
    private static final Vector VECTOR_ZERO = new Vector(0, 0, 0);
    private static final double EPSILON = 0.001;
    private static final boolean damageIndicatorPlayerTriggered = false;
    private static final int damageIndicatorTimeout = 40;

    public DamageIndicator(NewTMSv8 plugin) {
        this.plugin = plugin;
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        double finalDamage = event.getFinalDamage();
        boolean crit = false;
        //爆擊處理
        if(event.getDamageSource().getCausingEntity() instanceof Player player) {
            double critChance = plugin.getPlayerData(player).getCritChance();
            double critDamage = plugin.getPlayerData(player).getCritDamage();
            if(RANDOM.nextInt(100) < critChance){
                //爆擊觸發
                finalDamage  = finalDamage * ( 1 + critDamage/100 );
                event.setDamage(finalDamage);
                crit = true;
            }
        }
        if (!damageIndicatorPlayerTriggered) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.SUICIDE) || event.getFinalDamage() > Integer.MAX_VALUE) {
                return;
            }

            Entity entity = event.getEntity();

            String customName = CustomNameUtils.getMobCustomName(entity);
            if (customName != null) {
                for (String each : HoloMobHealth.disabledMobNamesAbsolute) {
                    if (customName.equals(ChatColorUtils.translateAlternateColorCodes('&', each))) {
                        return;
                    }
                }
                for (String each : HoloMobHealth.disabledMobNamesContains) {
                    if (ChatColorUtils.stripColor(customName.toLowerCase())
                            .contains(ChatColorUtils.stripColor(ChatColorUtils.translateAlternateColorCodes('&', each).toLowerCase()))) {
                        return;
                    }
                }
            }

            if (finalDamage >= HoloMobHealth.damageIndicatorDamageMinimum &&
                    entity instanceof LivingEntity &&
                    (EntityTypeUtils.getMobsTypesSet().contains(EntityTypeUtils.getEntityType(entity))
                            || EntityTypeUtils.getEntityType(entity).equals(EntityType.PLAYER))) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (MathUtils.greaterThan(livingEntity.getHealth(), 0.0, EPSILON) && !livingEntity.isDead()) {
                    damage(livingEntity, finalDamage,crit);
                }
            }
        }
    }

    //基本棄用
    @Deprecated
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (damageIndicatorPlayerTriggered) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.SUICIDE) || event.getFinalDamage() > Integer.MAX_VALUE) {
                return;
            }

            Entity entity = event.getEntity();

            if (!event.getDamager().getType().equals(EntityType.PLAYER)) {
                if (event.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) event.getDamager();
                    if (projectile.getShooter() == null) {
                        return;
                    } else {
                        if (!(projectile.getShooter() instanceof Player)) {
                            return;
                        }
                    }
                } else {
                    return;
                }
            }

            double finalDamage = event.getFinalDamage();
            if (finalDamage >= HoloMobHealth.damageIndicatorDamageMinimum && entity instanceof LivingEntity && (EntityTypeUtils.getMobsTypesSet().contains(EntityTypeUtils.getEntityType(entity)) || EntityTypeUtils.getEntityType(entity).equals(EntityType.PLAYER))) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (MathUtils.greaterThan(livingEntity.getHealth(), 0.0, EPSILON) && !livingEntity.isDead()) {
                    //damage(livingEntity, finalDamage);
                }
            }
        }
    }



    //顯示傷害的Function
    public void damage(LivingEntity entity, double damage,boolean crit) {
        Location location = entity.getLocation();

        double height = NMS.getInstance().getEntityHeight(entity);
        double width = NMS.getInstance().getEntityWidth(entity);

        double x;
        double y = height / 2 + (RANDOM.nextDouble() - 0.5) * 0.5;
        double z;
        if (RANDOM.nextBoolean()) {
            x = RANDOM.nextBoolean() ? width : -width;
            z = (RANDOM.nextDouble() * width) - (width / 2);
        } else {
            x = (RANDOM.nextDouble() * width) - (width / 2);
            z = RANDOM.nextBoolean() ? width : -width;
        }

        Vector velocity;
        location.add(0, y + HoloMobHealth.damageIndicatorDamageY, 0);
        Location indicator = location.clone().add(x, 0, z);

        velocity = VECTOR_ZERO;

        // 顯示傷害圖標的地方 下面就自訂了
        String colors = "&7";
        if(crit) colors = "&c&l";
        String text = "{Indicator_0.#}";
        String finalText = DisplayTextCacher.cacheDecimalFormat(colors(colors+text));//用HoloMob原始的方法轉換，就沒有另外寫了
        Component component = ParsePlaceholders.parse(entity, finalText, -damage);
        playIndicator(component, indicator, velocity, true, height);
    }


    public void playIndicator(Component entityNameComponent, Location location, Vector velocity, boolean gravity, double fallHeight) {
        Bukkit.getScheduler().runTaskAsynchronously(HoloMobHealth.plugin, () -> {
            int entityId;
            try {
                entityId = NMS.getInstance().getNextEntityId().get();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            UUID uuid = UUID.randomUUID();
            Location originalLocation = location.clone();

            PacketContainer[] packets = NMS.getInstance().createSpawnDamageIndicatorPackets(entityId, uuid, entityNameComponent, location, velocity, gravity);

            int range = HoloMobHealth.damageIndicatorVisibleRange;
            List<Player> players = location.getWorld().getPlayers().stream().filter(each -> {
                Location loc = each.getLocation();
                return loc.getWorld().equals(location.getWorld()) && loc.distance(location) <= range * range && HoloMobHealth.playersEnabled.contains(each);
            }).collect(Collectors.toList());

            PacketSender.sendServerPackets(players, packets);

            Vector downwardAccel = new Vector(0, -0.05, 0);

            new BukkitRunnable() {
                int tick = 0;

                @Override
                public void run() {
                    tick++;
                    if (!velocity.equals(VECTOR_ZERO) && tick < damageIndicatorTimeout && originalLocation.getY() - location.getY() < fallHeight) {
                        Vector drag = velocity.clone().normalize().multiply(-0.03);
                        if (gravity) {
                            velocity.add(downwardAccel);
                        }
                        velocity.add(drag);
                        location.add(velocity);

                        PacketContainer packet = NMS.getInstance().createEntityTeleportPacket(entityId, location);

                        PacketSender.sendServerPacket(players, packet);
                    } else if (tick >= damageIndicatorTimeout) {
                        this.cancel();
                        PacketContainer[] packets = NMS.getInstance().createEntityDestroyPacket(entityId);
                        Bukkit.getScheduler().runTaskLaterAsynchronously(HoloMobHealth.plugin, () -> {
                            PacketSender.sendServerPackets(players, packets);
                        }, 3);
                    }
                }
            }.runTaskTimerAsynchronously(HoloMobHealth.plugin, 0, 1);
        });
    }

}
