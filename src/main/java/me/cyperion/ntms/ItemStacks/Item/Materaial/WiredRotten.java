package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.ItemStacks.CraftRecipe;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;

import static me.cyperion.ntms.Utils.colors;

public class WiredRotten extends NTMSMaterial implements Listener {

    private Random random = new Random();
    public WiredRotten(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&f由各種意義上的東西組合成的肉塊，"));
        lore.add(colors("&f味道聞到後讓人有點反胃...，但吃下去"));
        lore.add(colors("&f消耗&62~3點飢餓值&f會回復&c8點血量&f..."));
        lore.add(colors(""));
        lore.add(colors("&8這個肉不能以一般方法吃掉喔。"));
        return lore;
    }

    @Override
    public Material getMaterailType() {
        return Material.ROTTEN_FLESH;
    }

    @Override
    public String getItemName() {
        return colors("&5破咒肉塊");
    }

    @Override
    public int getCustomModelData() {
        return 4006;
    }

    @Override
    public MaterailRate getMaterailRate() {
        return MaterailRate.RARE;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerEatingThis(PlayerInteractEvent event){
        if(event.getItem() == null) return;

        if(event.getItem().getType() != getMaterailType()) return;
        if(!event.getItem().hasItemMeta()) return;
        if(!(event.getAction() == Action.RIGHT_CLICK_AIR
                || event.getAction() == Action.RIGHT_CLICK_BLOCK))
            return;
        ItemStack item = event.getItem();
        if(item.getItemMeta().hasCustomModelData()
                && item.getItemMeta().getCustomModelData() == getCustomModelData()){
            event.setCancelled(true);
            if(event.getPlayer().getHealth() >=20) return;
            int reduce = random.nextInt(2,3+1);
            if(getMaterailRate() == MaterailRate.NORMAL) reduce = 4;//專為低階設計
            int food = event.getPlayer().getFoodLevel();
            if(food <= reduce) food= 0;else food -= reduce;
            event.getPlayer().setFoodLevel(food);

            event.getPlayer().addPotionEffect(new PotionEffect(
                    PotionEffectType.INSTANT_HEALTH,1,1,false,false));
            event.getPlayer().sendMessage(colors("&a你吃下了1個"+getItemName()));

            // 移除物品數量（吃一個）
            ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
            handItem.setAmount(handItem.getAmount() - 1);
            event.getPlayer().getInventory().setItemInMainHand(handItem);

        }
    }

    @Override
    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"WiredRotten"),toItemStack());
        recipe.shape(
                "xox",
                "oso",
                "xox");
        recipe.setIngredient('o', new RecipeChoice.ExactChoice(
                new EnchantedEnderPearl(plugin).toItemStack())
        );
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(
                new EnchantedRotten(plugin).toItemStack())
        );
        recipe.setIngredient('s',Material.CHICKEN);
        return recipe;
    }
}