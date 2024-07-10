package me.cyperion.ntms.ItemStacks.Item;

import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedRedstoneBlock;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 紅魔法杖的製作與武器本身<br>
 * 關聯：ItemRegister、/admin redwand、Explosion
 */
public class RedWand {

    private NewTMSv8 plugin;

    public RedWand(NewTMSv8 plugin) {
        this.plugin = plugin;
        init();
    }

    private ItemStack itemStack;
    private void init(){
        itemStack = new ItemStack(Material.STICK);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setCustomModelData(10001);//優先製作材質包用
        meta.setDisplayName("&6恵恵の紅魔法杖");
        ArrayList<String> lores = new ArrayList<>();
        lores.add(colors("&f嵌入&c紅寶石&f的普通木杖，閃耀的光芒、"));
        lores.add(colors("&f超帥的造型，正是紅魔族的最愛。拿在手中"));
        lores.add(colors("&f可以感受到了恵恵對爆裂魔法的執著。"));
        lores.add(colors("&c&l「上吧！お主様，此刻ーー即是爆破之時！」"));
        lores.add(colors("&c&l「我が力の奔流に望むは崩壊なり。」"));
        lores.add(colors(""));
        lores.add(colors("&d&l Explosion：&r&b需要選擇Explosion職業"));
        lores.add(colors("&f按住右鍵後將會開始充能，最後放開右鍵即可"));
        lores.add(colors("&f發射爆裂魔法，準心看到的點為中心大範圍"));
        lores.add(colors("&f造成&c600.0&f點傷害，有效射程為&a30&f格。"));
        lores.add(colors("&b魔力消耗&f: &b400 &3(允許透支魔力)"));
        lores.add(colors(""));
        lores.add(colors("&6傳說中的法杖"));
        meta.setLore(lores);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE,
                new AttributeModifier(new NamespacedKey(plugin,"damage_plus"),
                        5.0d,AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND.MAINHAND)
        );
        meta.addEnchant(Enchantment.KNOCKBACK,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);


    }

    public ShapedRecipe getRecipe(){
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"RedWand"),itemStack);
        recipe.shape(
                "XXO",
                "XAX",
                "AXX");
        recipe.setIngredient('X', new RecipeChoice.ExactChoice(new EnchantedRedstoneBlock(plugin).toItemStack()));
        recipe.setIngredient('O', new RecipeChoice.ExactChoice(new JadeCore().toItemStack()));
        recipe.setIngredient('A', Material.STICK);

        return recipe;
    }

    public ItemStack toItemStack(){
        return itemStack;
    }
}
