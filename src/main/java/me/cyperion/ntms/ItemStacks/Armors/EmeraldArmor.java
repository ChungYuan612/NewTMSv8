package me.cyperion.ntms.ItemStacks.Armors;

import me.cyperion.ntms.*;
import me.cyperion.ntms.ItemStacks.Item.Emerald_Coins;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * 綠寶石裝備
 * 關聯：ItemRegister、/admin
 */
public class EmeraldArmor implements PieceFullBouns , Listener {

    private final NewTMSv8 plugin;
    private ItemStack[] itemStack=new ItemStack[4];
    int luckAdd = 10;
    int[] touchnessAdd = new int[]{3,3,3,3};
    int[] armors = new int[]{4,8,6,4};
    EquipmentSlotGroup[] solts = new EquipmentSlotGroup[]{
            EquipmentSlotGroup.HEAD,
            EquipmentSlotGroup.CHEST,
            EquipmentSlotGroup.LEGS,
            EquipmentSlotGroup.FEET
    };
    public static final String ARMOR_FAMILY_EMERALD = "armor_family_emerald";

    public EmeraldArmor(NewTMSv8 plugin) {
        this.plugin = plugin;
        setEmeraldArmor();

    }

    public void setEmeraldArmor(){
        itemStack[0] = new ItemStack(Material.LEATHER_HELMET);

        itemStack[1] = new ItemStack(Material.LEATHER_CHESTPLATE);

        itemStack[2] = new ItemStack(Material.LEATHER_LEGGINGS);

        itemStack[3] = new ItemStack(Material.LEATHER_BOOTS);

        for(int i = 0; i < itemStack.length; i++){
            itemStack[i] = setArmorColor(itemStack[i]);
            LeatherArmorMeta emerald = (LeatherArmorMeta) itemStack[i].getItemMeta();
            emerald.setLore(getLores(i));
            emerald.setDisplayName(colors("&5"+"綠寶石"+armorNames[i]));
            emerald.addItemFlags(ItemFlag.HIDE_DYE);
            emerald.setUnbreakable(true);
            emerald.addAttributeModifier(Attribute.ARMOR,
                    new AttributeModifier(
                            new NamespacedKey(plugin,"armor_add"+ UUID.randomUUID()),
                            armors[i], AttributeModifier.Operation.ADD_NUMBER, solts[i]));
            emerald.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            new NamespacedKey(plugin,"armor_touchness_add"+ UUID.randomUUID()),
                            touchnessAdd[i], AttributeModifier.Operation.ADD_NUMBER, solts[i]));
            PersistentDataContainer container = emerald.getPersistentDataContainer();
            container.set(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_LUCK_ADD)
                    , PersistentDataType.INTEGER, luckAdd);

            container.set(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME)
                    , PersistentDataType.STRING, ARMOR_FAMILY_EMERALD);

            itemStack[i].setItemMeta(emerald);
        }


    }

    private ItemStack setArmorColor(ItemStack itemStack){
        LeatherArmorMeta emerald = (LeatherArmorMeta) itemStack.getItemMeta();
        emerald.setColor(Color.LIME);
        itemStack.setItemMeta(emerald);
        return itemStack;
    }

    String[] armorNames = {"帽子", "護甲", "長褲", "靴子"};
    private List<String> getLores(int solt){
        List<String> lores = new ArrayList<>();
        lores.add(colors("&f幸運值: &a+"+luckAdd+"點"));
        lores.add(colors(""));
        lores.add(colors("&f由&a&l綠寶石貨幣&r&f重鑄打造而成的"));
        lores.add(colors("&f裝備，感覺比鑽石甚至是獄髓還要硬"));
        lores.add(colors("&f!穿在身上可以增加&a幸運值&f。"));
        lores.add(colors(""));
        lores.add(colors("&6&l全套加成&r&f： &2&l寶石護盾牌 "));
        lores.add(colors("&f獲得&c定式回復I&f特殊效果，並且幸運值"));
        lores.add(colors("&f再加&a10&f點"));
        lores.add(colors(" "));
        lores.add(colors("&5&l史詩的"+armorNames[solt]));
        return lores;
    }

    public ItemStack[] getItemStacks() {
        return itemStack;
    }

    public ShapedRecipe[] toNMSRecipe(){
        ItemStack item = new Emerald_Coins().toItemStack();
        ShapedRecipe[] recipes = new ShapedRecipe[4];
        recipes[0] = new ShapedRecipe(new NamespacedKey(plugin,"EmeraldHelmet"),itemStack[0].clone());
        recipes[0].shape(
                "XXX",
                "X X",
                "   ");
        recipes[0].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[1] = new ShapedRecipe(new NamespacedKey(plugin,"EmeraldChestplate"),itemStack[1].clone());
        recipes[1].shape(
                "X X",
                "XXX",
                "XXX");
        recipes[1].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[2] = new ShapedRecipe(new NamespacedKey(plugin,"EmeraldLeggings"),itemStack[2].clone());
        recipes[2].shape(
                "XXX",
                "X X",
                "X X");
        recipes[2].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[3] = new ShapedRecipe(new NamespacedKey(plugin,"EmeraldBoots"),itemStack[3].clone());
        recipes[3].shape(
                "X X",
                "X X",
                "   ");
        recipes[3].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        return recipes;
    }

    String[] IDs = {"EmeraldBoots","EmeraldLeggings","EmeraldChestplate","EmeraldHelmet"};

    @Override
    public void checkAllArmor(Player player,ItemStack[] armors) {
        for(int i = 0;i<4;i++){
            boolean pass = false;
            if(armors[i] != null) {
                if (armors[i].hasItemMeta()
                        && armors[i].getItemMeta().getPersistentDataContainer().has(
                        plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_LUCK_ADD))
                        && armors[i].getItemMeta().getPersistentDataContainer().get(
                        plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME)
                        ,PersistentDataType.STRING).equals(ARMOR_FAMILY_EMERALD)
                ) {
                    if(plugin.getModifierMain().hasModifier(player,IDs[i])) continue;

                    int luckAdd = armors[i].getItemMeta()
                            .getPersistentDataContainer().get(
                                    plugin.getNsKeyRepo().getKey
                                            (NSKeyRepo.KEY_ARMOR_LUCK_ADD),
                                    PersistentDataType.INTEGER);
                    Modifier modifier = new Modifier(IDs[i],NSKeyRepo.KEY_PD_LUCK,ModifierType.ADD,luckAdd);
                    plugin.getModifierMain().addModifier(player,modifier);
                    pass = true;
                }
            }
            if(!pass){
                plugin.getModifierMain().removeModifier(player, IDs[i]);
            }

        }
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
                    ,PersistentDataType.STRING).equals(ARMOR_FAMILY_EMERALD)
            ) {
                //是不是Null
                //有無ItemMeta
                //有無NSkey(特殊裝備)
                //是不是綠寶石裝備
                return false;
            }
        }
        return true;
    }

    private static HashMap<UUID,Integer> emeraldFullSet = new HashMap<>();
    @Override
    public void addFullBouns(NewTMSv8 plugin,Player player) {
        if (!plugin.getModifierMain().hasModifier(player,fullSetModifierID)){
                plugin.getModifierMain().addModifier(
                        player,
                        new Modifier(
                                fullSetModifierID,
                                NSKeyRepo.KEY_PD_LUCK,
                                ModifierType.ADD,
                                10.0
                        )
                );
        }
        //寶石護盾效果
        emeraldFullSet.putIfAbsent(player.getUniqueId(),0);
        int count = emeraldFullSet.get(player.getUniqueId());
        count++;
        if(count == 4) {
            double health = player.getHealth();
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
            if(health +1 > maxHealth)
                player.setHealth(maxHealth);
            else
                player.setHealth(health + 1);
            emeraldFullSet.put(player.getUniqueId(),0);
            return;
        }
        emeraldFullSet.put(player.getUniqueId(),count);
    }

    private String fullSetModifierID = "emeraldarmor_fullset";
    @Override
    public void removeFullBouns(NewTMSv8 plugin,Player player) {
        if(plugin.getModifierMain().hasModifier(player,fullSetModifierID)) {
            plugin.getModifierMain().removeModifier(player, fullSetModifierID);
        }
    }

}
