package me.cyperion.ntms.ItemStacks.Tools;

import me.cyperion.ntms.ItemStacks.Item.Materaial.*;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

public class PureGoldenDarkSword {
    private final NewTMSv8 plugin;
    private ItemStack itemStack;

    public PureGoldenDarkSword(NewTMSv8 plugin) {
        this.plugin = plugin;
        initialize();
    }
    private void initialize(){
        itemStack = new ItemStack(Material.GOLDEN_SWORD);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(colors("&6&l純金黑長劍"));
        ArrayList<String > lore = new ArrayList<>();
        lore.add(colors("&f由&6純金&f+&8黑曜石&f打造而成的長劍"));
        lore.add(colors("&f傷害絕對是一流，但好像很容易"));
        lore.add(colors("&f壞掉，要小心使用!"));
        lore.add(colors(""));
        lore.add(colors("&c&l請注意這是有耐久消耗的物品"));
        lore.add(colors(""));
        lore.add(colors(MaterialRate.LEGENDARY.toLoreNoColor()+"長劍"));
        meta.setLore(lore);
        NamespacedKey mojangKey = NamespacedKey.minecraft("attack_damage");
//        NamespacedKey mojangKey2 = NamespacedKey.minecraft("attack_speed");
        meta.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                new AttributeModifier(
                        mojangKey,
                        8,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.MAINHAND)
        );
//        meta.addAttributeModifier(Attribute.ATTACK_SPEED,
//                new AttributeModifier(
//                        mojangKey2,
//                        1.6,
//                        AttributeModifier.Operation.ADD_NUMBER,
//                        EquipmentSlotGroup.MAINHAND)
//        );
        itemStack.setItemMeta(meta);
        itemStack.addUnsafeEnchantment(Enchantment.SHARPNESS,6);
        itemStack.addUnsafeEnchantment(Enchantment.UNBREAKING,10);

    }

    public ItemStack getItemStack() {
        return itemStack;
    }
    public ShapedRecipe getNMSRecipe(){
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"PureGoldenDarkSword"),this.getItemStack());
        recipe.shape("oxo","sxs","oao");
        recipe.setIngredient('x', new RecipeChoice.ExactChoice(new GoldenEssence(plugin).toItemStack()));
        recipe.setIngredient('o', new RecipeChoice.ExactChoice(new EnchantedObsidian(plugin).toItemStack()));
        recipe.setIngredient('s', new RecipeChoice.ExactChoice(new ReinfinedLapis(plugin).toItemStack()));
        recipe.setIngredient('a', Material.STICK);
        return recipe;
    }
}
