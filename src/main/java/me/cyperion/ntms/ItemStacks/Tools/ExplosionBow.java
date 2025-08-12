package me.cyperion.ntms.ItemStacks.Tools;

import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedString;
import me.cyperion.ntms.ItemStacks.Item.Materaial.GoldenEssence;
import me.cyperion.ntms.ItemStacks.Item.Materaial.MaterialRate;
import me.cyperion.ntms.ItemStacks.Item.Materaial.ReinfinedLapis;
import me.cyperion.ntms.ItemStacks.Item.TreasureCore;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;

import static me.cyperion.ntms.Utils.colors;

public class ExplosionBow implements Listener {
    private static final int CMD_EXPLOSION_BOW = 7101;
    private ItemStack itemStack;
    private NewTMSv8 plugin;

    public ExplosionBow(NewTMSv8 plugin) {
        this.plugin = plugin;
        init();
    }

    public void init(){
        itemStack = new ItemStack(Material.BOW,1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(colors("&6&l爆炸弓"));
        ArrayList<String> lores = new ArrayList<>();
        lores.add(colors("&f一把火藥味十足的弓，射出去的"));
        lores.add(colors("&f箭矢都會&c發生爆炸&f，或許搭配著"));
        lores.add(colors("&dTerminator&f會讓這個效果"));
        lores.add(colors("&f變得更帥！&8而且這個可以炸人"));
        lores.add(colors(""));
        lores.add(colors("&6&l被動技能&r&f：&c爆炸射擊"));
        lores.add(colors("&f將&dTerminator&f的箭矢轉換"));
        lores.add(colors("&f為爆炸箭矢，但是增加&b魔力消耗 &31&f 點"));
        lores.add(colors(""));
        lores.add(colors("&c&l請注意這是有耐久消耗的物品"));
        lores.add(colors(""));
        lores.add(colors(getMaterialRate().toLoreNoColor()+"長弓"));
        itemMeta.setLore(lores);
        itemMeta.setCustomModelData(CMD_EXPLOSION_BOW);
        itemStack.setItemMeta(itemMeta);
    }

    private MaterialRate getMaterialRate() {
        return MaterialRate.EPIC;
    }

    public ItemStack getItemStack(){
        return itemStack.clone();
    }

    public ShapedRecipe toNMSRecipe(){
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"ExplosionBow"),this.getItemStack());
        recipe.shape(" ox",
                     "a x",
                     " ox");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(new EnchantedString(plugin).toItemStack()));
        recipe.setIngredient('a', new RecipeChoice.ExactChoice(new TreasureCore(plugin).toItemStack()));
        recipe.setIngredient('o', new RecipeChoice.ExactChoice(new ReinfinedLapis(plugin).toItemStack()));
        return recipe;
    }

    public static boolean isExplosionBow(ItemStack itemStack){
        if(itemStack.getType() != Material.BOW) return false;
        if(!itemStack.hasItemMeta()) return false;
        if(!itemStack.getItemMeta().hasCustomModelData()) return false;
        return itemStack.getItemMeta().getCustomModelData() == CMD_EXPLOSION_BOW;
    }

    @EventHandler
    public void onExplosionBowShot(ProjectileHitEvent event){
        Location location = event.getEntity().getLocation();
        if(!(event.getEntity().getShooter() instanceof Player)) return;
        if(!(event.getEntity() instanceof Arrow))return;
        Player player = (Player) event.getEntity().getShooter();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(!isExplosionBow(item)) return;

        location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2);
        location.getWorld().spawnParticle(Particle.FLAME, location, 1, 0.5, 0.5, 0.5, 0.02);
        Collection<Entity> nearbyEntities= location.getWorld().getNearbyEntities(location, 2.6, 2.6, 2.6, entity -> {
            if(entity instanceof Monster || entity instanceof Player)
                return true;
            return false;
        });
        for (Entity entity : nearbyEntities) {
            if(entity instanceof Monster monster && monster.isValid() && !monster.isDead()) {
                // 計算從中心往外的方向向量
                Vector knockback = monster.getLocation().toVector().subtract(location.toVector()).normalize().multiply(0.65);
                knockback.setY(0.2); // 增加一點垂直力，模仿爆風
                monster.setVelocity(knockback);
                monster.damage(8,player);
            }
            if(entity instanceof Player player1 && player1.isValid() && !player1.isDead()) {
                player1.damage(2,player);
            }
        }
        event.getEntity().remove();
    }
}
