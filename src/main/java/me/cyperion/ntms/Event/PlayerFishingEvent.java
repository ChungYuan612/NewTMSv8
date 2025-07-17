package me.cyperion.ntms.Event;

import me.cyperion.ntms.ItemStacks.Item.JadeCore;
import me.cyperion.ntms.ItemStacks.Item.MysteryTurtleEgg;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.cyperion.ntms.Utils.colors;

public class PlayerFishingEvent implements Listener {

    private NewTMSv8 plugin;

    private final Random random = new Random();
    private final JadeCore jadeCore = new JadeCore();
    private final MysteryTurtleEgg mysteryTurtleEgg = new MysteryTurtleEgg();
    private final List<FishingReward> fishingRewardList = new ArrayList<>();
    private final DecimalFormat df = new DecimalFormat("#.###");

    public PlayerFishingEvent(NewTMSv8 plugin) {
        this.plugin = plugin;
        fishingRewardList.add(new FishingReward(jadeCore.toItemStack(), 0.85d,1.0d));
        fishingRewardList.add(new FishingReward(null, 3d,3.5d));
        fishingRewardList.add(new FishingReward(mysteryTurtleEgg.toItemStack(), 0.01d,0.011d));
    }

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event){


        if(!event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
            return;
        for(FishingReward reward : fishingRewardList){
            double rate = reward.rate;
            if(!event.getPlayer().getLocation().getWorld().isClearWeather())
                rate = reward.rainyRate;
            //防止釣魚機
            if(!event.getHook().isInOpenWater())
                rate /=3;

            Player player = event.getPlayer();
            double value;
            double base = rate;
            double luckbouns = plugin.getPlayerData(player).getLuck();
            if(luckbouns <= 0) value = rate;
            else value = rate * (1+luckbouns/100);//機率門檻

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
                    if(value < 0.05d)
                        Bukkit.broadcastMessage(colors("&6[廣播] &b"+event.getPlayer().getDisplayName()+"&f 釣起了 "+reward.reward.getItemMeta().getDisplayName()+"&f!"));

                }
            }
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
