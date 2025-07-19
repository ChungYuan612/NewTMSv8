package me.cyperion.ntms.Class;

import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import net.minecraft.core.Holder;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

import static me.cyperion.ntms.Utils.colors;

public class Bard extends Class implements Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 7 * 1000;
    private Random random = new Random();
    MusicInstrument[] musicInstruments;
    public Bard(NewTMSv8 plugin) {
        super(plugin);
        musicInstruments = new MusicInstrument[7];
        musicInstruments[0] = MusicInstrument.SEEK_GOAT_HORN;
        musicInstruments[1] = MusicInstrument.YEARN_GOAT_HORN;
        musicInstruments[2] = MusicInstrument.ADMIRE_GOAT_HORN;
        musicInstruments[3] = MusicInstrument.FEEL_GOAT_HORN;
        musicInstruments[4] = MusicInstrument.PONDER_GOAT_HORN;
        musicInstruments[5] = MusicInstrument.SING_GOAT_HORN;
        musicInstruments[6] = MusicInstrument.CALL_GOAT_HORN;
    }

    @Override
    public ClassType getClassType() {
        return ClassType.BARD;
    }

    @Override
    public String getName() {
        return colors("&f吟遊詩人");
    }

    @Override
    public ItemStack getIcon() {
        ItemStack bard;
        bard = new ItemStack(Material.GOAT_HORN);
        ItemMeta bardMeta = bard.getItemMeta();
        bardMeta.setDisplayName(plugin.getBard().getName());
        ArrayList<String> bardLore = new ArrayList<>();
        bardLore.add(colors(""));
        bardLore.add(colors("&6&l職業技能&r&f：&3&lBard"));
        bardLore.add(colors("&f玩家只要拿著&d各類山羊角&f吹響即可施放"));
        bardLore.add(colors("&f技能，為周圍的所有玩家提供為期&a15&f秒的&c回復&f、"));
        bardLore.add(colors("&e吸收&f、&5力量&f效果，並且擊退周圍&38格的敵人&f等"));
        bardLore.add(colors(""));
        bardLore.add(colors("&b&l每個山羊角都有特殊的效果："));
        bardLore.add(colors("&f- 尋覓&6：&f消耗&b"+seekCostMana+"&f點&3魔力&f，讓附近&c50&f格的突襲者發光並虛弱，持續&a30&f秒，突襲BOSS則不算"));
        bardLore.add(colors("&f- 嚮往&6：&f消耗&b"+yearnCostMana+"&f點&3魔力&f，把附近&c15&f格的敵人擊退並減少敵人的移動速度&a40%&f。"));
        bardLore.add(colors("&f- 仰慕&6：&f消耗&b"+admireCostMana+"&f點&3魔力&f，給予附近&c12&f格的隊友與&c回復&f與&e吸收&f效果，持續&a20&f秒。"));
        bardLore.add(colors("&f- 感覺&6：&f消耗&b"+feelCostMana+"&f點&3魔力&f，移除&c15&f格內所有玩家的&8負面&f效果"));
        bardLore.add(colors("&f- 沉思&6：&f消耗&b"+ponderCostMana+"&f點&3魔力&f，讓周圍&c12格&f所有玩家立即回復&b15&f點&3魔力"));
        bardLore.add(colors("&f- 歌頌&6：&f消耗&b"+singCostMana+"&f點&3魔力&f，給予周圍&c12格&f所有玩家&4力量&f效果，並給予自身&7抗性&f效果，持續&a20&f秒"));
        bardLore.add(colors("&f- 呼喚&6：&f消耗&b"+callCostMana+"&f點&3魔力&f，給予周圍&c12格&f所有玩家&2速度加成&f效果與&a跳躍提升&f效果，持續&a30&f秒"));
        bardLore.add(colors("&f- 夢想&6：&f消耗&b"+dreamCostMana+"&f點&3魔力&f，獲得上述隨機效果!"));
        bardLore.add(colors(""));

        bardMeta.setLore(bardLore);
        bardMeta.setCustomModelData(1010);
        bardMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bard.setItemMeta(bardMeta);
        return bard.clone();
    }
    final int seekCostMana = 15;
    final int yearnCostMana = 12;
    final int admireCostMana = 16;
    final int feelCostMana = 15;
    final int ponderCostMana = 20;
    final int singCostMana = 20;
    final int callCostMana = 14;
    final int dreamCostMana = 14;

    /**
     * 山羊角列表
     * Seek（尋覓）	    seek_goat_horn
     * Yearn（嚮往）	    yearn_goat_horn
     * Admire（仰慕）	admire_goat_horn
     * Feel（感覺）	    feel_goat_horn
     * Ponder（沉思）	ponder_goat_horn
     * Sing（歌頌）	    sing_goat_horn
     * Call（呼喚）	    call_goat_horn
     * Dream（夢想）	    dream_goat_horn
     */

    //當玩家吹號角
    @EventHandler
    public void onPlayerBlowHorn(PlayerInteractEvent event) {

        if (plugin.getPlayerData(event.getPlayer()).getClassType() != ClassType.BARD) return;

        // 避免觸發兩次（副手與主手）
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getItem() == null || event.getItem().getType() != Material.GOAT_HORN) return;

        Player player = event.getPlayer();

        ItemStack item = event.getItem();

        UUID uuid = player.getUniqueId();
        // 初始化玩家冷卻資料
        cooldowns.putIfAbsent(uuid, 0L);
        long now = System.currentTimeMillis();
        long lastUsed = cooldowns.get(uuid);
        if (now - lastUsed < COOLDOWN_TIME) {
            long secondsLeft = (COOLDOWN_TIME - (now - lastUsed)) / 1000;
            player.sendMessage("§6[錯誤] §c技能還在冷卻中！剩餘 §6" + secondsLeft + "§c 秒");
            event.setCancelled(true);
            return;
        }
        // 記錄冷卻時間
        cooldowns.put(uuid, now);

        if(player.hasCooldown(Material.GOAT_HORN)) {
            player.sendMessage("§6[錯誤] §c技能還在冷卻中！");
            event.setCancelled(true);
            return;
        }

        MusicInstrument musicInstrument = null;
        if(item.getItemMeta() instanceof MusicInstrumentMeta meta){
            musicInstrument = meta.getInstrument();
        }
        if(musicInstrument == null) return;

        if(musicInstrument.equals(MusicInstrument.DREAM_GOAT_HORN)) {//夢想
            if(plugin.getMana().costMana(player,dreamCostMana)) {
                musicInstrument = getRandomMusicInstrument();
            }
        }

        String id = "";
        if(musicInstrument == MusicInstrument.SEEK_GOAT_HORN){//尋覓
            id = "尋覓";
            if(plugin.getMana().costMana(player,seekCostMana)){
                List<Entity> entities = player.getNearbyEntities(50, 50, 50);
                for(Entity entity : entities){
                    if(entity instanceof Raider raider){
                        raider.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 30, 0));
                        raider.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 30, 0));
                    }
                }
            }
        }else if(musicInstrument==MusicInstrument.YEARN_GOAT_HORN) {//嚮往
            id = "嚮往";
            if(plugin.getMana().costMana(player,yearnCostMana)){
                List<Entity> entities = player.getNearbyEntities(15, 15, 15);
                for (Entity entity : entities) {
                    if (entity instanceof Monster monster) {
                        monster.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 12, 1));
                    }
                }
                knockbackMonsters(player.getLocation(), 10, 0.6);
            }
        }else if(musicInstrument==MusicInstrument.ADMIRE_GOAT_HORN) {//仰慕
            id = "仰慕";
            if(plugin.getMana().costMana(player,admireCostMana)){
                List<Entity> entities = player.getNearbyEntities(12, 12, 12);
                entities.add(player);
                for (Entity entity : entities) {
                    if (entity instanceof Player target) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 20, 1));
                        target.getWorld().spawnParticle(Particle.HEART, target.getLocation(), 5, 0.5, 0.5, 0.5, 0.1);
                    }
                }
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 1);
            }
        }else if(musicInstrument==MusicInstrument.FEEL_GOAT_HORN) {//感覺
            id = "感覺";
            if(plugin.getMana().costMana(player,feelCostMana)){
                List<Entity> entities = player.getNearbyEntities(15, 15, 15);
                entities.add(player);
                int count = 0;
                for (Entity entity : entities) {
                    if (entity instanceof Player nearbyPlayer) {
                        boolean hasPurify = false;
                        for (PotionEffect effect : nearbyPlayer.getActivePotionEffects()) {
                            if (!isNegativeEffect(effect.getType())) continue;
                            nearbyPlayer.removePotionEffect(effect.getType());
                            hasPurify = true;
                        }
                        if (!hasPurify) continue;
                        nearbyPlayer.sendMessage("§a你的負面狀態被§b"+player.getName()+"§a的技能淨化了！");
                        nearbyPlayer.playSound(nearbyPlayer.getLocation(), Sound.ENTITY_SPLASH_POTION_BREAK, 1, 1);
                        count++;
                    }
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 0.8f, 1);
                player.sendMessage(colors("&a你幫&b"+count+"&a人淨化了負面藥水效果"));
            }
        }else if(musicInstrument==MusicInstrument.PONDER_GOAT_HORN) {//沉思
            id = "沉思";
            if(plugin.getMana().costMana(player,ponderCostMana)){
                List<Entity> entities = player.getNearbyEntities(12, 12, 12);
                int count = 0;
                for (Entity entity : entities) {
                    if (entity instanceof Player nearbyPlayer) {
                        plugin.getMana().addMana(nearbyPlayer,15);
                        nearbyPlayer.sendMessage("§b"+player.getName()+"§a幫你回復了§315§a點魔力！");
                        nearbyPlayer.playSound(nearbyPlayer.getLocation(), Sound.ENTITY_WITCH_DRINK, 1, 1);
                        count++;
                    }
                }
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 0.8f, 1);
                player.sendMessage(colors("&a你幫&b"+count+"&a人回復了魔力"));
            }
        }else if(musicInstrument==MusicInstrument.SING_GOAT_HORN) {//歌頌
            id = "歌頌";
            if(plugin.getMana().costMana(player,singCostMana)){
                List<Entity> entities = player.getNearbyEntities(12, 12, 12);
                for (Entity entity : entities) {
                    if (entity instanceof Player target) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 20, 1));
                        target.getWorld().spawnParticle(Particle.FLAME, target.getLocation(), 15, 0.5, 0.5, 0.5, 0.5);
                    }
                }
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 20, 1,false,false));
                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.6f, 0.8f);

            }
        }else if(musicInstrument==MusicInstrument.CALL_GOAT_HORN) {//呼喚
            id = "呼喚";
            if(plugin.getMana().costMana(player,callCostMana)){
                List<Entity> entities = player.getNearbyEntities(12, 12, 12);
                entities.add(player);
                for (Entity entity : entities) {
                    if (entity instanceof Player target) {
                        target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 30, 0));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 30, 1));
                        target.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, target.getLocation(), 15, 0.5, 0.5, 0.5, 0.5);
                    }
                }
                player.playSound(player.getLocation(), Sound.ITEM_HONEY_BOTTLE_DRINK, 1, 1);
            }
        }
        if(random.nextInt(100) ==0){
            player.sendMessage(colors("&b"+id));
            id = "&dI春日影I &6!";
        }
        player.sendMessage(colors("&a您演奏了 &b"+id));

    }



    /**
     * 夢想山羊角需要隨機山羊角效果
     */
    private MusicInstrument getRandomMusicInstrument() {
        Random random = new Random();
        int index = random.nextInt(musicInstruments.length);
        return musicInstruments[index];
    }

    /**
     * 擊退生物
     */
    public void knockbackMonsters(Location center, double radius, double strength) {
        World world = center.getWorld();
        if (world == null) return;

        for (Entity entity : world.getNearbyEntities(center, radius, radius, radius)) {
            if (entity instanceof Monster monster && monster.isValid() && !monster.isDead()) {
                // 計算從中心往外的方向向量
                Vector knockback = monster.getLocation().toVector().subtract(center.toVector()).normalize().multiply(strength);
                knockback.setY(0.8); // 增加一點垂直力，模仿爆風
                monster.setVelocity(knockback);
            }
        }
    }

    // 判斷是否為負面效果的方法
    private boolean isNegativeEffect(PotionEffectType type) {
        return type.equals(PotionEffectType.BLINDNESS)
                || type.equals(PotionEffectType.HUNGER)
                || type.equals(PotionEffectType.POISON)
                || type.equals(PotionEffectType.SLOWNESS)
                || type.equals(PotionEffectType.MINING_FATIGUE)
                || type.equals(PotionEffectType.WEAKNESS)
                || type.equals(PotionEffectType.WITHER)
                || type.equals(PotionEffectType.UNLUCK)
                || type.equals(PotionEffectType.DARKNESS);
    }


}