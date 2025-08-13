package me.cyperion.ntms.Monster;

import me.cyperion.ntms.Event.PlayerFishingEvent;
import me.cyperion.ntms.Event.RaidEvent;
import me.cyperion.ntms.ItemStacks.Item.Emerald_Coins;
import me.cyperion.ntms.ItemStacks.Item.LauNaFishingRod;
import me.cyperion.ntms.ItemStacks.Item.Materaial.GoldenEssence;
import me.cyperion.ntms.ItemStacks.Item.Materaial.ReinfinedLapis;
import me.cyperion.ntms.ItemStacks.Item.MysteryTurtleEgg;
import me.cyperion.ntms.ItemStacks.Item.Stocks;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.SideBoard.NTMSEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.function.Consumer;

import static me.cyperion.ntms.Monster.LootItem.chanceIn;
import static me.cyperion.ntms.Utils.colors;

/**
 * 特殊怪物生成
 * 關聯：NewTMSv8註冊
 */
public class MonsterRegister implements Listener {
    public static HashMap<LivingEntity,Creature> twMobs = new HashMap<>();
    private final NewTMSv8 plugin;
    private final boolean customMobSpawn = false;
    private final LootItem raidLapis;//突襲掉落物 先放這裡
    private final LootItem emerald;//突襲掉落物 先放這裡
    private final LootItem goldenEssence;//突襲掉落物 先放這裡

    private final LootItem normalEmerald;//突襲掉落物 先放這裡
    private final LootItem normalEmeraldBlock;//突襲掉落物 先放這裡

    private final LootItem fishingRodBase;//釣魚掉落物(老衲) 先放這裡
    private final LootItem mysteryTurtleEgg;//釣魚掉落物(老衲) 先放這裡

    public MonsterRegister(NewTMSv8 plugin) {
        this.plugin = plugin;

        //突襲掉落物 先放這裡
        normalEmerald = new LootItem(new ItemStack(Material.EMERALD),1,8,75);
        normalEmeraldBlock = new LootItem(new ItemStack(Material.EMERALD_BLOCK),1,3,35);
        raidLapis = new LootItem(new ReinfinedLapis(plugin).toItemStack(),1,1,4);
        emerald = new LootItem(new Emerald_Coins().toItemStack(),1,1,0.1);
        goldenEssence = new LootItem(new GoldenEssence(plugin).toItemStack(),1,1,5);

        mysteryTurtleEgg = new LootItem(new MysteryTurtleEgg().toItemStack(),1,1,0.4);
        fishingRodBase = new LootItem(new LauNaFishingRod(plugin).toItemStack(),1,1,7);
    }


    @EventHandler
    public void onMobSpawning(CreatureSpawnEvent event){

        if(event.getEntity() instanceof Vex vex){
            if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPELL
             || event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.REINFORCEMENTS){

                vex.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE,20*90,1,true,false));
                vex.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE,20*90,0,true,false));
                vex.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,20*10,0,true,false));
                vex.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,20*90,0,true,false));
            }
        }


        if(customMobSpawn && event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.NATURAL)){
            LivingEntity entity = event.getEntity();
            Location location = entity.getLocation();
            Location check = location.clone();
            check.setY(location.getY()-1);
            if(!location.getWorld().getName().equals(plugin.MAIN_WORLD_NAME)) return;
            //本島內
            if (check.getX() > 3000 || check.getX() <=-2000 ) return ;
            if (check.getZ() > 3000 || check.getZ() <=-2000 ) return ;


            if(!check.getBlock().getType().equals(Material.GRASS_BLOCK)){
                if (chanceIn(Creature.LOG_ZOMBIES.getSpawnChance())){
                    spawnTwMob(Creature.LOG_ZOMBIES,location);
                    System.out.println(colors("&c原木求魚已生成"));
                    event.setCancelled(true);
                    return;
                }
            }
            if (chanceIn(Creature.ILLUSIONER.getSpawnChance())){
                spawnTwMob(Creature.ILLUSIONER,location);
                System.out.println(colors("&c幻術師已生成"));
                event.setCancelled(true);
                return;
            }

        }

    }

    public static void spawnTwMob(Creature creature,Location location) {
        twMobs.put(creature.spawn(location),creature);
    }

    private Random random = new Random();
    @EventHandler
    public void onMobDeathing(EntityDeathEvent event) {
        if(event.getEntity().hasMetadata(RaidEvent.META_RAID_BUFF)){
            //event.getDrops().clear();
            raidLapis.tryDropLoot(event.getEntity().getLocation());
            emerald.tryDropLoot(event.getEntity().getLocation());
            //伏守者
            if(event.getEntity().getType() == EntityType.WARDEN){
                event.getDrops().clear();
                goldenEssence.tryDropLoot(event.getEntity().getLocation());
                normalEmeraldBlock.tryDropLoot(event.getEntity().getLocation());
                normalEmerald.tryDropLoot(event.getEntity().getLocation());
                Collection<Entity> entityCollection = event.getEntity().getWorld().getNearbyEntities(event.getEntity().getLocation(), 100, 100, 100);
                for(Player player: Bukkit.getOnlinePlayers()){
                    if(!player.getWorld().equals(plugin.getTWWorld())) continue;
                    if(!entityCollection.contains(player)) continue;
                    double rewardCoins = random.nextInt(300);
                    //突襲警報的話金額要x2.5
                    if(plugin.getNtmsEvents().getNowEvent().equals(NTMSEvents.EventType.RADI_BONUS_EVENT))
                        rewardCoins *= 2.5;
                    plugin.getEconomy().depositPlayer(player, rewardCoins);
                    player.sendMessage(colors("&6[突襲資訊] &a成功討伐&d奇厄伏守者&a，獲得了&6"+rewardCoins+"&a元的獎金！"));
                }
            }
            return;
        }else if(event.getEntity().hasMetadata(PlayerFishingEvent.META_FISHING_BUFF)){
            event.getDrops().clear();
            mysteryTurtleEgg.tryDropLoot(event.getEntity().getLocation());
            fishingRodBase.tryDropLoot(event.getEntity().getLocation());
            plugin.getLogger().info("老衲死了");
            //分紅
            for(Player player: Bukkit.getOnlinePlayers()){
                if(!player.getWorld().equals(event.getEntity().getWorld())) continue;
                if(player.getLocation().distance(event.getEntity().getLocation()) > 50) continue;
                double rewardCoins = random.nextInt(50,200);
                plugin.getEconomy().depositPlayer(player, rewardCoins);
                player.sendMessage(colors("&6[釣魚] &7&l老衲&r&a身上掉落了一些錢，獲得了&6"+rewardCoins+"&a元的分紅！"));
            }

        }
        if(!customMobSpawn) return;
        if (!twMobs.containsKey(event.getEntity())) return;
        event.getDrops().clear();
        Creature mob = twMobs.get(event.getEntity());
        mob.tryLootItem(event.getEntity().getLocation());
        event.setDroppedExp(mob.getExperience());
        twMobs.remove(event.getEntity());
        if(twMobs.isEmpty()) return;
        List<LivingEntity> removelist = new ArrayList<>();
        for(LivingEntity entity: twMobs.keySet()) {
            if(entity.isDead() || !(entity.isValid())) {
                removelist.add(entity);
            }
        }
        removelist.forEach(Entity::remove);

    }

    @EventHandler
    public void onZombieSwap(CreatureSpawnEvent event) {
        if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.DROWNED)) {
            if (event.getEntity().isCustomNameVisible()) {
                event.getEntity().damage(100);
            }
        }
    }



}
