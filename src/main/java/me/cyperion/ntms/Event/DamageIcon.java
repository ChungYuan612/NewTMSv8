package me.cyperion.ntms.Event;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;

import static me.cyperion.ntms.Utils.colors;

/**
 * 主要控制傷害顯示，把受到的傷害像Skyblock顯示出來<br>
 * Runnable在建構式就註冊上去了，主程式NewTMSv8註冊事件即可<br>
 * <p>關聯：NewTMSv8註冊</p>
 */
public class DamageIcon implements Listener {

    private NewTMSv8 plugin;
    public static HashMap<Entity,Integer> damageIcons = new HashMap<>();
    //顯示的格式(因為數字可能比較小，所以有小數)
    private DecimalFormat formatter = new DecimalFormat("#.#");
    private static final int TICK_STEP = 2;
    public DamageIcon(NewTMSv8 plugin) {
        this.plugin = plugin;

        new BukkitRunnable(){
            private final Set<Entity> damageIconsKeySet = damageIcons.keySet();
            private final List<Entity> removeList = new ArrayList<>();
            @Override
            public void run() {
                for (Entity icon :damageIconsKeySet){
                    int timeleft = damageIcons.get(icon);
                    if(timeleft <=0){
                        icon.remove();
                        removeList.add(icon);
                        continue;
                    }
                    timeleft--;
                    damageIcons.put(icon,timeleft);
                }
                removeList.forEach(damageIconsKeySet::remove);
            }
        }.runTaskTimer(plugin,0L,1L * TICK_STEP);
    }

    @EventHandler
    public void onDamageIcon(EntityDamageEvent event) {
        String damageCode = "&7";
        try{
            if(event.getDamageSource().equals(EntityDamageEvent.DamageCause.FIRE)
                    || event.getDamageSource().equals(EntityDamageEvent.DamageCause.FIRE_TICK)
                    || event.getDamageSource().equals(EntityDamageEvent.DamageCause.LAVA)) {
                damageCode = "&6";
            }
        }catch (Exception e){
            System.out.println("[DamageIcon] Something went wrong! " + e);
        }

        //實體受傷主要程式
        double damage = event.getFinalDamage();
        World world = event.getEntity().getWorld();
        String finalDamageCode = damageCode;
        world.spawn(
                event.getEntity().getLocation().clone().add(getRandomOffSet(),1,getRandomOffSet()),
                ArmorStand.class,
                armorStand -> {
                    armorStand.setSmall(true);
                    armorStand.setCustomNameVisible(true);
                    armorStand.setCustomName(colors(finalDamageCode + formatter.format(damage)));
                    armorStand.setMarker(true);
                    armorStand.setGravity(false);
                    armorStand.setVisible(false);
                    damageIcons.put(armorStand,30/TICK_STEP);
                }
        );
    }

    public double getRandomOffSet() {
        double number = Math.random();
        if(Math.random()>0.5) number *= -1;
        return number;
    }
}