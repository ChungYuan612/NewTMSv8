package me.cyperion.ntms.ItemStacks.Armors;

import me.cyperion.ntms.ItemStacks.Item.Materaial.ReinfinedLapis;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

public class LapisArmor implements PieceFullBouns {

    private NewTMSv8 plugin;
    private ItemStack[] itemStack;
    int[] manaAddRate = new int[]{10,20,15,5};
    int[] armors = new int[]{4,7,5,4};
    EquipmentSlotGroup[] solts = new EquipmentSlotGroup[]{
            EquipmentSlotGroup.HEAD,
            EquipmentSlotGroup.CHEST,
            EquipmentSlotGroup.LEGS,
            EquipmentSlotGroup.FEET
    };

    public LapisArmor(NewTMSv8 plugin) {
        this.plugin = plugin;
        setLapisArmor();

    }

    public void setLapisArmor(){
        itemStack[0] = new ItemStack(Material.LEATHER_HELMET);

        itemStack[1] = new ItemStack(Material.LEATHER_CHESTPLATE);

        itemStack[2] = new ItemStack(Material.LEATHER_LEGGINGS);

        itemStack[3] = new ItemStack(Material.LEATHER_BOOTS);

        for(int i = 0; i < itemStack.length; i++){
            itemStack[i] = setArmorColor(itemStack[i]);
            LeatherArmorMeta lapis = (LeatherArmorMeta) itemStack[i].getItemMeta();
            lapis.setLore(getLores(i));
            lapis.setDisplayName(colors("&9"+"青金石"+armorNames[i]));
            lapis.addItemFlags(ItemFlag.HIDE_DYE);
            lapis.setUnbreakable(true);
            lapis.addAttributeModifier(Attribute.ARMOR,
                    new AttributeModifier(
                            new NamespacedKey(plugin,"armor_add"),
                            armors[i], AttributeModifier.Operation.ADD_NUMBER, solts[i]));
            lapis.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            new NamespacedKey(plugin,"armor_touchness_add"),
                            2, AttributeModifier.Operation.ADD_NUMBER, solts[i]));
            PersistentDataContainer container = lapis.getPersistentDataContainer();
            container.set(
                    plugin.getNsKeyRepo().getKey(plugin.getNsKeyRepo().KEY_ARMOR_MANA_ADD)
                    , PersistentDataType.INTEGER, manaAddRate[i]);

        }


    }

    private ItemStack setArmorColor(ItemStack itemStack){
        LeatherArmorMeta lapis = (LeatherArmorMeta) itemStack.getItemMeta();
        lapis.setColor(Color.BLUE);
        itemStack.setItemMeta(lapis);
        return itemStack;
    }

    String[] armorNames = {"帽子", "法袍", "長褲", "皮靴"};
    private List<String> getLores(int solt){
        List<String> lores = new ArrayList<>();
        lores.add(colors("&f魔力上限: &b+"+manaAddRate[solt])+"點");
        lores.add(colors(""));
        lores.add(colors("&f由突襲中掉落的&9青金石&7打造而成的裝備"));
        lores.add(colors("&f穿在身上可以增加&b魔力值&f"));
        lores.add(colors(""));
        lores.add(colors("&6&l全套加成&r&f： &2&l查克拉回復法 &r&e(蹲下啟用)"));
        lores.add(colors("&f蹲下的時候魔力回復速度將會&b+100%&f，"));
        lores.add(colors("&f但是會獲得&c緩速&f效果"));
        lores.add(colors(""));
        lores.add(colors("&7&o有人叫你動了嗎?"));
        lores.add(colors(" "));
        lores.add(colors("&5&l史詩的"+armorNames[solt]));
        return lores;
    }

    public ItemStack[] getItemStacks() {
        return itemStack;
    }

    private ShapedRecipe[] toNMSRecipe(){
        ItemStack item = new ReinfinedLapis(plugin).toItemStack();
        ShapedRecipe[] recipes = new ShapedRecipe[4];
        recipes[0] = new ShapedRecipe(new NamespacedKey(plugin,"LapisHelmet"),itemStack[0]);
        recipes[0].shape(
                "XXX",
                "X X",
                "   ");
        recipes[0].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[1] = new ShapedRecipe(new NamespacedKey(plugin,"LapisChestplate"),itemStack[1]);
        recipes[1].shape(
                "X X",
                "XXX",
                "XXX");
        recipes[1].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[2] = new ShapedRecipe(new NamespacedKey(plugin,"LapisLeggings"),itemStack[2]);
        recipes[2].shape(
                "XXX",
                "X X",
                "X X");
        recipes[2].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[3] = new ShapedRecipe(new NamespacedKey(plugin,"LapisBoots"),itemStack[3]);
        recipes[3].shape(
                "X X",
                "X X",
                "   ");
        recipes[3].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        return recipes;
    }



    @Override
    public void checkAllArmor(Player player,ItemStack[] armors) {
        int manaAdd = 0;
        for(int i = 0;i<4;i++){
            if(armors[i] == null || !armors[i].isSimilar(itemStack[i])) {
                if (armors[i].hasItemMeta()
                        && armors[i].getItemMeta().getPersistentDataContainer().has(
                        plugin.getNsKeyRepo().getKey(plugin.getNsKeyRepo().KEY_ARMOR_MANA_ADD)))
                    manaAdd += manaAddRate[i];
            }
        }
        if(manaAdd > 0 &&
                manaAdd != plugin.getPlayerData(player).getMaxMana() - PlayerData.DEFAULT_MAX_MANA)
            plugin.getPlayerData(player).setMaxMana((manaAdd));
    }

    @Override
    public boolean isFullSet(ItemStack[] armors) {
        for(int i = 0;i<4;i++){
            if(armors[i] == null || !armors[i].isSimilar(itemStack[i])){
                return false;
            }
        }
        return true;
    }

    @Override
    public void addFullBouns(NewTMSv8 plugin,Player player) {
        //這裡先直接指定用 TODO
        if(plugin.getPlayerData(player).getManaReg() == 1)
            plugin.getPlayerData(player).setManaReg(2);
    }
    @Override
    public void removeFullBouns(NewTMSv8 plugin,Player player) {
        //這裡直接指定用 TODO
        if(plugin.getPlayerData(player).getManaReg() == 2)
            plugin.getPlayerData(player).setManaReg(1);

    }
}