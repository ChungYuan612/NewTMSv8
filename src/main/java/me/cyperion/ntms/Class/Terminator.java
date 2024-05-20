package me.cyperion.ntms.Class;

import me.cyperion.ntms.NewTMSv8;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import static me.cyperion.ntms.Utils.colors;

public class Terminator extends Class implements Listener {

    public double costManaOnShot = 3;
    public Terminator(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    public ClassType getClassType() {
        return ClassType.TERMINATOR;
    }

    public String getName() {
        return colors("&dTerminator");
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event){
        Player player = event.getPlayer();

        if(plugin.getPlayerData(player)
                .getClassType() != ClassType.TERMINATOR){
            return;
        }

        if(event.getAction() == Action.LEFT_CLICK_BLOCK
         || event.getAction() == Action.LEFT_CLICK_AIR) {
            //確定點擊左鍵
            if(player.getInventory()
                    .getItemInMainHand().getType()
                    == Material.BOW) {
                //確定是弓了

                boolean success = plugin.getMana().costMana(player,costManaOnShot);
                if (success) {
                    Vector direction = player.getLocation().getDirection();
                    Arrow arrow = player.launchProjectile(Arrow.class, direction.multiply(5));

                    if (isTripleShot(player)) {
                        shootArrow(arrow.getLocation().add(direction.multiply(2)), direction.rotateAroundY(Math.PI / 4));
                        shootArrow(arrow.getLocation().add(direction.multiply(-2)), direction.rotateAroundY(-Math.PI / 4));
                    }else if (isPentaShot(player)) {
                        shootArrow(arrow.getLocation().add(direction.multiply(2)), direction.rotateAroundY(Math.PI / 4));
                        shootArrow(arrow.getLocation().add(direction.multiply(-2)), direction.rotateAroundY(-Math.PI / 4));
                        shootArrow(arrow.getLocation().add(direction.multiply(4)), direction.rotateAroundY(Math.PI));
                        shootArrow(arrow.getLocation().add(direction.multiply(-4)), direction.rotateAroundY(-Math.PI));
                    }
                    shootArrow(arrow.getLocation(), direction);
                    player.playSound(player.getLocation(),
                            Sound.ENTITY_SKELETON_SHOOT, 1, 1);

                }
            }
        }
    }

    private boolean isPentaShot(Player player) {
        return plugin.getPlayerData(player).getPerkSecond() == 2;
    }

    private boolean isTripleShot(Player player) {
        return plugin.getPlayerData(player).getPerkSecond() == 1;
    }

    private void shootArrow(Location location, Vector direction) {
        Arrow arrow = location.getWorld().spawn(location, Arrow.class);
        arrow.setVelocity(direction);
    }
}
