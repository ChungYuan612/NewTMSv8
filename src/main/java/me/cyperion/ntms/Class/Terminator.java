package me.cyperion.ntms.Class;

import me.cyperion.ntms.NewTMSv8;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

public class Terminator extends Class implements Listener {

    private final HashMap<UUID,Integer> playerSteps = new HashMap<>();

    public double costManaOnShot = 3;
    public Terminator(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    public ClassType getClassType() {
        return ClassType.TERMINATOR;
    }

    @Override
    public String getName() {
        return colors("&dTerminator");
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(!plugin.getPlayerData(player)
                .getClassType().equals(ClassType.TERMINATOR)){
            return;
        }

        if(event.getAction() == Action.LEFT_CLICK_BLOCK
         || event.getAction() == Action.LEFT_CLICK_AIR) {
            //確定點擊左鍵
            if(player.getInventory()
                    .getItemInMainHand().getType()
                    == Material.BOW) {
                //確定是弓了

                if(!playerSteps.containsKey(player.getUniqueId())){
                    playerSteps.put(player.getUniqueId(),0);
                }

                boolean success = plugin.getMana().costMana(player,costManaOnShot);
                if (success) {
                    //發射三支箭矢，一支箭矢往前，另外左右15度的地方各射出一支

                    Location location = player.getEyeLocation();
                    ItemStack itemStack = player.getInventory().getItemInMainHand();
                    int step = playerSteps.get(player.getUniqueId());
                    playerSteps.replace(player.getUniqueId(),(step+1)%3);
                    boolean isThird = step == 2;

                    Vector direction = player.getVelocity();
                    shootArrow(player,itemStack, location,direction,isThird);
                    shootArrow(player,itemStack,location,direction.rotateAroundY(15),isThird);
                    shootArrow(player,itemStack,location,direction.rotateAroundY(-15),isThird);
                    player.playSound(player.getLocation(),
                            Sound.ENTITY_SKELETON_SHOOT, 0.5f, 1);

                    if(isThird){
                        player.playSound(player.getLocation(),
                                Sound.ENTITY_SKELETON_AMBIENT, 0.5f, 1);
                    }

                }
            }
        }
    }

    /**
     * Terminator職業被動技能：
     * 總共3層，第3層時重新計算並提高傷害15%，並中毒3秒。
     */
    private void shootArrow(Player player, ItemStack itemStack, Location location, Vector direction,boolean isThird) {
        Arrow arrow = location.getWorld().spawn(location, Arrow.class);
        arrow.setShooter(player);
        if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchant(Enchantment.FLAME)){
            arrow.setVisualFire(true);
        }

        if(isThird)
            arrow.setColor(Color.ORANGE);
        arrow.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 3, 0), false);
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        arrow.setVelocity(direction);

    }
}
