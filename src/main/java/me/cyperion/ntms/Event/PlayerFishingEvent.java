package me.cyperion.ntms.Event;

import me.cyperion.ntms.ItemStacks.Item.JadeCore;
import me.cyperion.ntms.ItemStacks.Item.LauNaFishingRod;
import me.cyperion.ntms.ItemStacks.Item.MysteryTurtleEgg;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.SideBoard.NTMSEvents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import java.net.MalformedURLException;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * 處理玩家釣魚相關的所有程式，包含海怪生成
 */

public class PlayerFishingEvent implements Listener {

    private NewTMSv8 plugin;

    private final Random random = new Random();
    private final JadeCore jadeCore = new JadeCore();
    private final MysteryTurtleEgg mysteryTurtleEgg = new MysteryTurtleEgg();
    private final List<FishingReward> fishingRewardList = new ArrayList<>();
    private final DecimalFormat df = new DecimalFormat("#.###");
    public static final String META_FISHING_BUFF = "fishing_buff";

    private final int lauNaHealth = 40;

    public PlayerFishingEvent(NewTMSv8 plugin) {
        this.plugin = plugin;
        fishingRewardList.add(new FishingReward(jadeCore.toItemStack(), 0.85d,1.0d));
        fishingRewardList.add(new FishingReward(null, 3d,3.5d));
        fishingRewardList.add(new FishingReward(mysteryTurtleEgg.toItemStack(), 0.02d,0.022d));
    }

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event){


        if(!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
            return;

        boolean isFishingBonusEvent = plugin.getNtmsEvents().getNowEvent() == NTMSEvents.EventType.FISHING_BONUS_EVENT;
        if(isFishingBonusEvent && random.nextInt(100) <= 1){
            event.getPlayer().sendMessage(colors("&6[稀有釣魚] &f老衲被你釣上來了"));
            plugin.getLogger().info(event.getPlayer().getName()+"釣上來了老衲");
            LivingEntity lauNa = spawnLauNa(event.getHook().getLocation());
            // 計算向玩家方向的向量
            Location hookLoc = event.getHook().getLocation();
            Location playerLoc = event.getPlayer().getLocation().add(0, 1.5, 0); // 模擬釣鉤目標點
            Vector velocity = playerLoc.toVector().subtract(hookLoc.toVector()).normalize().multiply(1.5); // 拉力強度調整

            // 套用速度（拉過去）
            lauNa.setVelocity(velocity);

            // 若原本釣到魚，移除之
            if (event.getCaught() != null) {
                event.getCaught().remove();
            }
            return;
        }

        for(FishingReward reward : fishingRewardList){
            double rate = reward.rate;
            if(!event.getPlayer().getLocation().getWorld().isClearWeather())
                rate = reward.rainyRate;
            //防止釣魚機
            if(!event.getHook().isInOpenWater())
                rate /=3;
            if(isFishingBonusEvent){
                rate *=1.5;//釣魚祭
            }
            Player player = event.getPlayer();
            double value = rate;
            double base = rate;
            double luckbouns = plugin.getPlayerData(player).getLuck();
            if(luckbouns > 0) value *= (1+luckbouns/100);//機率門檻

            double v = Math.random() * 100;
            if(v < value){//這個v在機率門檻內

                if(reward.reward == null){
                    double coins = random.nextDouble(50,1200);
                    plugin.getEconomy().depositPlayer(player, coins);
                    if(luckbouns > 0)
                        player.sendMessage(colors("&6[稀有釣魚] &e"
                                +String.format("%,.0f",coins)+"元 &b("+df.format(base)+"&2+"+df.format(value-base)+"&b%)&f!"));
                    else
                        player.sendMessage(colors("&6[稀有釣魚] &e"
                                +String.format("%,.0f",coins)+"元 &b("+df.format(base)+"%)&f!"));
                    plugin.getLogger().info(colors(event.getPlayer().getDisplayName() + " 釣起了 "
                            +String.format("%,.0f",coins)+"元 &b("+(base)+"&2+"+df.format(value-base)+"&b%)&f!"));
                }else if(event.getCaught() instanceof Item item){
                    plugin.getLogger().info(event.getPlayer().getDisplayName() + " 釣起了 "+reward.reward.getItemMeta().getDisplayName()+"! "+v+" in 100");
                    item.setItemStack(reward.reward.clone());
                    if(luckbouns > 0)
                        player.sendMessage(colors("&6[稀有釣魚] &f"
                                +reward.reward.clone().getItemMeta().getDisplayName()+" &b("+df.format(base)+"&2+"+df.format(value-base)+"&b%)&f!"));
                    else
                        player.sendMessage(colors("&6[稀有釣魚] &f"
                                +reward.reward.clone().getItemMeta().getDisplayName()+" &b("+df.format(base)+"%)&f!"));
                    if(value < 0.1d)
                        Bukkit.broadcastMessage(colors("&6[廣播] &b"+event.getPlayer().getDisplayName()+"&f 釣起了 "+reward.reward.getItemMeta().getDisplayName()+"&f!"));

                }
            }
        }

    }

    public LivingEntity spawnLauNa(Location location){
        LivingEntity lauNa = (LivingEntity) location.getWorld().spawnEntity(location, EntityType.DROWNED);
        lauNa.setCustomNameVisible(true);
        lauNa.setCustomName(colors("&7&l老衲"));
        lauNa.getAttribute(Attribute.MAX_HEALTH).setBaseValue(lauNaHealth);
        lauNa.setHealth(lauNaHealth);

        EntityEquipment equipment = lauNa.getEquipment();

        LauNaFishingRod lauNaFishingRod = new LauNaFishingRod(plugin);
        ItemStack mainHandItem = lauNaFishingRod.toItemStack();
        equipment.setItemInMainHand(mainHandItem);
        equipment.setItemInMainHandDropChance(0f);

        equipment.setHelmet(getLauNaHead());
        equipment.setHelmetDropChance(0f);

        equipment.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        equipment.setChestplateDropChance(0f);

        equipment.setLeggingsDropChance(0f);

        equipment.setChestplate(new ItemStack(Material.DIAMOND_BOOTS));
        equipment.setBootsDropChance(0f);

        lauNa.setMetadata(META_FISHING_BUFF, new FixedMetadataValue(plugin, true));

        return lauNa;

    }

    public ItemStack getLauNaHead(){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(getProfile());
        head.setItemMeta(meta);
        return head.clone();
    }

    private PlayerProfile getProfile(){
        try{
            UUID uuid = UUID.fromString("ab34f543-d9dd-53a6-9cef-2c563f453548");
            PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
            PlayerTextures textures = profile.getTextures();
            String skinUrl = "http://textures.minecraft.net/texture/e3226f11e5075f1c5da184df99d97762df3ee015db0404fdad40eafc03087f9f";
            textures.setSkin(URI.create(skinUrl).toURL());
            profile.setTextures(textures);
            return profile;
        }catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    static class FishingReward{
        ItemStack reward;
        double rate,rainyRate;

        public FishingReward(ItemStack reward, double rate,double rainyRate){
            this.reward = reward;
            this.rate = rate;
            this.rainyRate = rainyRate;
        }

    }
}
