package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class SurvivalSpecialBlock implements Listener {

    private NewTMSv8 plugin;
    public SurvivalSpecialBlock(NewTMSv8 plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void ShowLight(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() == Material.LIGHT &&
                player.getGameMode() != GameMode.CREATIVE)
        {
            Location location = player.getLocation().getBlock().getLocation();

            for (int x = -3; x < 3; x += 1)
            {
                for (int y = -3; y < 3; y += 1)
                {
                    for (int z = -3; z < 3; z += 1)
                    {
                        Block current = location.clone().add(x, y, z).getBlock();

                        if (current.getType() == Material.LIGHT)
                        {SpawnMarkForBlock(current);}
                    }
                }
            }
        }
    }



    static void SpawnMarkForBlock(Block current)
    {
        current.getWorld().spawnParticle
                (
                        Particle.BLOCK_MARKER,
                        current.getLocation().add(0.5, 0.5, 0.5),
                        1, current.getBlockData()
                );
    }

    /**
     * 處理玩家互動事件（左鍵點擊）
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // 檢查是否為左鍵點擊方塊
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        // 檢查玩家是否手持特殊方塊
        if (!isHoldingSpecialBlock(player)) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        // 檢查被點擊的方塊是否為特殊方塊
        if (isSpecialBlock(clickedBlock.getType())) {
            // 取消事件，防止正常的破壞行為
            event.setCancelled(true);

            // 直接破壞方塊，不掉落任何物品
            Material originalType = clickedBlock.getType();
            clickedBlock.setType(Material.AIR);

            player.sendMessage("§c已移除 " + originalType.name() + "！");
        }
    }

    private boolean isHoldingSpecialBlock(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        return item != null && (item.getType() == Material.LIGHT || item.getType() == Material.STRUCTURE_VOID);
    }

    /**
     * 檢查方塊是否為特殊方塊
     */
    private boolean isSpecialBlock(Material material) {
        return material == Material.LIGHT  || material == Material.STRUCTURE_VOID;
    }


}
