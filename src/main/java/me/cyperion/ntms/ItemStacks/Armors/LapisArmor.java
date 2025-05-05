package me.cyperion.ntms.ItemStacks.Armors;

import me.cyperion.ntms.ItemStacks.Item.Materaial.ReinfinedLapis;
import me.cyperion.ntms.Mana;
import me.cyperion.ntms.NSKeyRepo;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * 青金石裝備
 * 關聯：ItemRegister、/admin
 */
public class LapisArmor implements PieceFullBouns , Listener {

    private final NewTMSv8 plugin;
    private ItemStack[] itemStack=new ItemStack[4];
    int[] manaAddRate = new int[]{10,20,15,5};
    int[] touchnessAdd = new int[]{3,4,3,3};
    int[] armors = new int[]{4,7,5,4};
    EquipmentSlotGroup[] solts = new EquipmentSlotGroup[]{
            EquipmentSlotGroup.HEAD,
            EquipmentSlotGroup.CHEST,
            EquipmentSlotGroup.LEGS,
            EquipmentSlotGroup.FEET
    };
    public static final String ARMOR_FAMILY_LAPIS = "armor_family_lapis";

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
                            new NamespacedKey(plugin,"armor_add"+ UUID.randomUUID()),
                            armors[i], AttributeModifier.Operation.ADD_NUMBER, solts[i]));
            lapis.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            new NamespacedKey(plugin,"armor_touchness_add"+ UUID.randomUUID()),
                            touchnessAdd[i], AttributeModifier.Operation.ADD_NUMBER, solts[i]));
            PersistentDataContainer container = lapis.getPersistentDataContainer();
            container.set(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_MANA_ADD)
                    , PersistentDataType.INTEGER, manaAddRate[i]);

            container.set(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME)
                    , PersistentDataType.STRING, ARMOR_FAMILY_LAPIS);

            itemStack[i].setItemMeta(lapis);
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
        lores.add(colors("&f由突襲中掉落的&9青金石&f打造而成的裝備"));
        lores.add(colors("&f穿在身上可以增加&b魔力值&f"));
        lores.add(colors(""));
        lores.add(colors("&6&l全套加成&r&f： &2&l查克拉回復法 &r&e(蹲下啟用)"));
        lores.add(colors("&f蹲下的時候魔力回復速度將會&b+100%&f，"));
        lores.add(colors("&f但是會獲得&c緩速&f效果"));
        lores.add(colors(" "));
        lores.add(colors("&5&l史詩的"+armorNames[solt]));
        return lores;
    }

    public ItemStack[] getItemStacks() {
        return itemStack;
    }

    public ShapedRecipe[] toNMSRecipe(){
        ItemStack item = new ReinfinedLapis(plugin).toItemStack();
        ShapedRecipe[] recipes = new ShapedRecipe[4];
        recipes[0] = new ShapedRecipe(new NamespacedKey(plugin,"LapisHelmet"),itemStack[0].clone());
        recipes[0].shape(
                "XXX",
                "X X",
                "   ");
        recipes[0].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[1] = new ShapedRecipe(new NamespacedKey(plugin,"LapisChestplate"),itemStack[1].clone());
        recipes[1].shape(
                "X X",
                "XXX",
                "XXX");
        recipes[1].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[2] = new ShapedRecipe(new NamespacedKey(plugin,"LapisLeggings"),itemStack[2].clone());
        recipes[2].shape(
                "XXX",
                "X X",
                "X X");
        recipes[2].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[3] = new ShapedRecipe(new NamespacedKey(plugin,"LapisBoots"),itemStack[3].clone());
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
            if(armors[i] != null) {
                if (armors[i].hasItemMeta()
                        && armors[i].getItemMeta().getPersistentDataContainer().has(
                        plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_MANA_ADD)))
                    manaAdd += armors[i].getItemMeta()
                            .getPersistentDataContainer().get(
                                    plugin.getNsKeyRepo().getKey
                                            (NSKeyRepo.KEY_ARMOR_MANA_ADD),
                                    PersistentDataType.INTEGER);
            }
        }
        if(manaAdd != plugin.getPlayerData(player).getMaxMana() - Mana.defaultMaxMana)
            plugin.getPlayerData(player).setMaxMana(manaAdd + Mana.defaultMaxMana);
    }

    @Override
    public boolean isFullSet(ItemStack[] armors) {
        for(int i = 0;i<4;i++){
            if(armors[i] == null
                    || !armors[i].hasItemMeta()
                    || !armors[i].getItemMeta().getPersistentDataContainer().has(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME))
                    || !armors[i].getItemMeta().getPersistentDataContainer().get(
                                   plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME)
                            ,PersistentDataType.STRING).equals(ARMOR_FAMILY_LAPIS)
            ) {
                //是不是Null
                //有無ItemMeta
                //有無NSkey(特殊裝備)
                //是不是青金石裝備
                return false;
            }
        }
        return true;
    }

    @Override
    public void addFullBouns(NewTMSv8 plugin,Player player) {
        //這裡先直接指定用 TODO
        if(player.isSneaking()) {
            if (plugin.getPlayerData(player).getManaReg() == 1){
                plugin.getPlayerData(player).setManaReg(2);
                player.addPotionEffect(
                        new PotionEffect(PotionEffectType.SLOWNESS,9000,3,true,false)
                );
            }

        }else if (plugin.getPlayerData(player).getManaReg() == 2){
            plugin.getPlayerData(player).setManaReg(1);
            if(player.hasPotionEffect(PotionEffectType.SLOWNESS)
                    && player.getPotionEffect(PotionEffectType.SLOWNESS).getAmplifier() == 3)
                player.removePotionEffect(PotionEffectType.SLOWNESS);
        }


    }
    @Override
    public void removeFullBouns(NewTMSv8 plugin,Player player) {
        //這裡直接指定用 TODO
        if(plugin.getPlayerData(player).getManaReg() == 2){
            plugin.getPlayerData(player).setManaReg(1);
            if(player.hasPotionEffect(PotionEffectType.SLOWNESS)
                    && player.getPotionEffect(PotionEffectType.SLOWNESS).getAmplifier() == 3)
                player.removePotionEffect(PotionEffectType.SLOWNESS);
        }
    }

}