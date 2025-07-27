package me.cyperion.ntms.ItemStacks.Item;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 老衲的超高速釣竿
 * 跟其他物品一樣，註冊在ItemRegister，然後老衲被擊殺那裏有引用
 */
public class LauNaFishingRod {
    private ItemStack itemStack;
    private ItemStack itemStack2;

    private NewTMSv8 plugin;

    public LauNaFishingRod(NewTMSv8 plugin) {
        this.plugin = plugin;
        init();
    }


    public void init(){
        itemStack = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(colors("&8&l&o老衲的超快速釣竿原型"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors(""));
        lore.add(colors("&7經過特殊改良的釣竿"));
        lore.add(colors("&7目前還只是原型而已"));
        lore.add(colors("&7沒有什麼功能"));
        lore.add(colors(""));
        lore.add(colors("&c&l取得方式：&r&7擊殺老衲"));
        lore.add(colors("&c&l本物品禁止附上任何附魔、修改名字"));
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.setCustomModelData(10002);
        itemStack.setItemMeta(meta);

        itemStack2 = new ItemStack(Material.FISHING_ROD);
        ItemMeta meta2 = itemStack.getItemMeta();
        meta2.setDisplayName(colors("&8&l老衲的超快速釣竿"));
        ArrayList<String> lore2 = new ArrayList<>();
        lore2.add(colors(""));
        lore2.add(colors("&7經過特殊改良的釣竿，"));
        lore2.add(colors("&7已經不需要再附魔了。"));
        lore2.add(colors(""));
        lore2.add(colors("&c&l取得方式：&r&7合成"));
        meta2.setLore(lore2);
        meta2.setCustomModelData(10003);
        meta2.setUnbreakable(true);
        meta2.addItemFlags(ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_UNBREAKABLE);
        itemStack2.setItemMeta(meta2);
        itemStack2.addUnsafeEnchantment(Enchantment.LURE,5);
        itemStack2.addEnchantment(Enchantment.LUCK_OF_THE_SEA,3);
    }

    public ShapedRecipe getRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, "LauNaFishingRod"), itemStack2);
        recipe.shape(
                "XXX",
                "XAX",
                "XXX");
        recipe.setIngredient('X',new RecipeChoice.ExactChoice(new JadeCore().toItemStack()));
        recipe.setIngredient('A',new RecipeChoice.ExactChoice(this.itemStack.clone()));
        return recipe;
    }

    /**
     *
     * @return 初級釣竿材料
     */
    public ItemStack toItemStack(){
        return itemStack.clone();
    }

    /**
     *
     * @return 老衲的釣竿
     */
    public ItemStack toItemStackPlus(){
        return itemStack2.clone();
    }
}
