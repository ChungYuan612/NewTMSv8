package me.cyperion.ntms.ItemStacks.Item;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import static me.cyperion.ntms.ItemStacks.ItemRegister.*;
import static me.cyperion.ntms.Utils.colors;

public class EnderCrystal {
    ItemStack item,frag;
    private NewTMSv8 plugin;
    public EnderCrystal(NewTMSv8 plugin) {
        this.plugin = plugin;
        initFrag();
        initItem();
    }

    private void initItem() {
        item = new ItemStack(Material.QUARTZ,1);
        ItemMeta meta =  item.getItemMeta();
        meta.setDisplayName(colors("&e&l純白終界水晶"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&f攻擊力: &c+1點"));
        lore.add(colors(""));
        lore.add(colors("&6&l被動技能&r&f： &2強化&7(副手觸發)"));
        lore.add(colors("&f投射物對&5終界龍&f造成的傷害"));
        lore.add(colors("&f額外增加&a20%&f。"));
        lore.add(colors(""));
        lore.add(colors("&6傳說的物品"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(
                        new NamespacedKey(plugin, "strength" + UUID.randomUUID()),
                        1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.OFFHAND));
        meta.setCustomModelData(CMD_PURE_WHITE_END_CRYSTAL);
        item.setItemMeta(meta);
    }

    private void initFrag(){
        frag = new ItemStack(Material.AMETHYST_SHARD,1);
        ItemMeta meta =  frag.getItemMeta();
        meta.setDisplayName(colors("&d終界水晶碎片"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&f從&5龍戰&f中低機率取得的"));
        lore.add(colors("&f純白色的終界水晶，好像"));
        lore.add(colors("&f可以用來製作&5龍戰&f的物品"));
        lore.add(colors(""));
        lore.add(colors("&5罕見的材料"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(CMD_PURE_WHITE_END_CRYSTAL_FRAGMENT);
        frag.setItemMeta(meta);

    }

    public ShapedRecipe getRecipe(){
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"EnderCrystal"),item.clone());
        recipe.shape(
                "xxx",
                "xox",
                "xxx");
        recipe.setIngredient('x',new RecipeChoice.ExactChoice(frag.clone()));
        recipe.setIngredient('o',Material.END_CRYSTAL);

        return recipe;

    }

    public boolean isHoldingThis(Player player){
        try{
            ItemStack itemStack = player.getInventory().getItemInOffHand();
            if(itemStack.hasItemMeta() &&
                    itemStack.getItemMeta().getCustomModelData()
                            == CMD_PURE_WHITE_END_CRYSTAL){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }


    public ItemStack toItemStack(){
        return item.clone();
    }
    public ItemStack toItemStackFragment(){
        return frag.clone();
    }

}
