package me.cyperion.ntms.ItemStacks.Armors;

import me.cyperion.ntms.ItemStacks.Item.Materaial.EnchantedObsidian;
import me.cyperion.ntms.ItemStacks.Item.Materaial.MaterialRate;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

public class ObsidianChestplate {

    private final NewTMSv8 plugin;

    public ObsidianChestplate(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public ItemStack toItemStack() {
        ItemStack itemStack = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta itemMeta = (LeatherArmorMeta) itemStack.getItemMeta();
        itemMeta.setColor(Color.BLACK);
        itemMeta.setDisplayName(colors("&f黑曜石胸甲"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&7由許多堅硬的"+new EnchantedObsidian(plugin).getItemName()+"&7組成，保護力"));
        lore.add(colors("&7可謂相當驚人!"));
        lore.add(colors(""));
        lore.add(colors(MaterialRate.EPIC.toLoreNoColor() + "胸甲"));
        itemMeta.setLore(lore);

        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_DYE);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        itemMeta.addAttributeModifier(Attribute.ARMOR
                ,new AttributeModifier(
                        new NamespacedKey(plugin,"obsidian_armor_add_"+ UUID.randomUUID()),
                        8,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                )
        );
        itemMeta.addAttributeModifier(Attribute.ARMOR_TOUGHNESS
                ,new AttributeModifier(
                        new NamespacedKey(plugin,"obsidian_armor_touchness_add_"+ UUID.randomUUID()),
                        6,
                        AttributeModifier.Operation.ADD_NUMBER,
                        EquipmentSlotGroup.CHEST
                )
        );
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ShapedRecipe toNMSRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin,"ObsidianChestplate"),this.toItemStack());
        recipe.shape("X X","XXX","XXX");
        recipe.setIngredient('X', new RecipeChoice.ExactChoice(new EnchantedObsidian(plugin).toItemStack()));
        return recipe;
    }
}
