package me.cyperion.ntms.ItemStacks.Armors;

import me.cyperion.ntms.*;
import me.cyperion.ntms.ItemStacks.Item.EnderCrystal;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * 龍裔裝備
 * 關聯：ItemRegister註冊事件等、/admin
 */
public class DragonArmor implements PieceFullBouns, Listener {

    private final NewTMSv8 plugin;
    private ItemStack[] itemStack = new ItemStack[4];
    double critChanceAdd = 10;
    double critDamageAdd = 20;
    int damageAdd = 1;
    double speedAdd = 0.2; // 20% 移動速度增加
    int[] touchnessAdd = new int[]{3, 3, 3, 3};
    int[] armors = new int[]{4, 8, 6, 4};
    EquipmentSlotGroup[] solts = new EquipmentSlotGroup[]{
            EquipmentSlotGroup.HEAD,
            EquipmentSlotGroup.CHEST,
            EquipmentSlotGroup.LEGS,
            EquipmentSlotGroup.FEET
    };
    public static final String ARMOR_FAMILY_DRAGON = "armor_family_dragon";

    public DragonArmor(NewTMSv8 plugin) {
        this.plugin = plugin;
        setDragonArmor();
    }

    public void setDragonArmor() {
        itemStack[0] = new ItemStack(Material.LEATHER_HELMET);
        itemStack[1] = new ItemStack(Material.LEATHER_CHESTPLATE);
        itemStack[2] = new ItemStack(Material.LEATHER_LEGGINGS);
        itemStack[3] = new ItemStack(Material.LEATHER_BOOTS);

        for (int i = 0; i < itemStack.length; i++) {
            itemStack[i] = setArmorColor(itemStack[i]);
            LeatherArmorMeta dragon = (LeatherArmorMeta) itemStack[i].getItemMeta();



            dragon.setLore(getLores(i));
            dragon.setDisplayName(colors("&5" + "龍之" + armorNames[i]));
            dragon.addItemFlags(ItemFlag.HIDE_DYE,ItemFlag.HIDE_ARMOR_TRIM);
            dragon.setUnbreakable(true);

            // 基礎護甲值
            dragon.addAttributeModifier(Attribute.ARMOR,
                    new AttributeModifier(
                            new NamespacedKey(plugin, "armor_add" + UUID.randomUUID()),
                            armors[i], AttributeModifier.Operation.ADD_NUMBER, solts[i]));
            dragon.addAttributeModifier(Attribute.ARMOR_TOUGHNESS,
                    new AttributeModifier(
                            new NamespacedKey(plugin, "armor_touchness_add" + UUID.randomUUID()),
                            touchnessAdd[i], AttributeModifier.Operation.ADD_NUMBER, solts[i]));

            PersistentDataContainer container = dragon.getPersistentDataContainer();
            container.set(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME),
                    PersistentDataType.STRING, ARMOR_FAMILY_DRAGON);

            // 根據部位添加不同屬性
            switch (i) {
                case 0: // 頭盔 - 爆擊機率
                    container.set(
                            plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_CRIT_CHANCE_ADD),
                            PersistentDataType.DOUBLE, critChanceAdd);
                    break;
                case 1: // 胸甲 - 力量1點
                    dragon.addAttributeModifier(Attribute.ATTACK_DAMAGE,
                            new AttributeModifier(
                                    new NamespacedKey(plugin, "strength" + UUID.randomUUID()),
                                    damageAdd, AttributeModifier.Operation.ADD_NUMBER, solts[i]));
                    break;
                case 2: // 褲子 - 爆擊傷害
                    container.set(
                            plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_CRIT_DAMAGE_ADD),
                            PersistentDataType.DOUBLE, critDamageAdd);
                    break;
                case 3: // 鞋子 - 移動速度
                    dragon.addAttributeModifier(Attribute.MOVEMENT_SPEED,
                            new AttributeModifier(
                                    new NamespacedKey(plugin, "speed_add" + UUID.randomUUID()),
                                    speedAdd, AttributeModifier.Operation.MULTIPLY_SCALAR_1, solts[i]));
                    break;
            }

            itemStack[i].setItemMeta(dragon);
        }
    }

    private ItemStack setArmorColor(ItemStack itemStack) {
        LeatherArmorMeta dragon = (LeatherArmorMeta) itemStack.getItemMeta();
        dragon.setColor(Color.BLACK);
        itemStack.setItemMeta(dragon);

        ArmorMeta meta = (ArmorMeta) itemStack.getItemMeta();
        // 添加 ArmorTrim
        ArmorTrim trim = new ArmorTrim(TrimMaterial.AMETHYST, TrimPattern.EYE);
        meta.setTrim(trim);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    String[] armorNames = {"頭盔", "胸甲", "護腿", "戰靴"};

    private List<String> getLores(int slot) {
        List<String> lores = new ArrayList<>();

        switch (slot) {
            case 0: // 頭盔
                lores.add(colors("&f爆擊機率: &9+" + critChanceAdd + "點"));
                break;
            case 1: // 胸甲
                lores.add(colors("&f攻擊力: &9+" + damageAdd + "點"));
                break;
            case 2: // 褲子
                lores.add(colors("&f爆擊傷害: &9+" + critDamageAdd + "點"));
                break;
            case 3: // 鞋子
                lores.add(colors("&f移動速度: &a+" + (int)(speedAdd * 100) + "%"));
                break;
        }

        lores.add(colors(""));
        lores.add(colors("&f由&5&l終界水晶&r&f重鑄打造而成的"));
        lores.add(colors("&f傳說裝備，蘊含著遠古龍族的力量"));
        lores.add(colors("&f，散發著神秘的紫色光芒。"));
        lores.add(colors(""));
        lores.add(colors("&6&l全套加成&r&f： &5&l龍裔瞄準"));
        lores.add(colors("&f箭矢會自動偵測&c5格&f範圍內的敵人"));
        lores.add(colors("&f並自動轉向追蹤目標"));
        lores.add(colors(" "));
        lores.add(colors("&6&l傳說的" + armorNames[slot]));
        return lores;
    }

    public ItemStack[] getItemStacks() {
        return itemStack.clone();
    }

    public ShapedRecipe[] toNMSRecipe() {
        ItemStack item = new EnderCrystal(plugin).toItemStack();
        ShapedRecipe[] recipes = new ShapedRecipe[4];

        recipes[0] = new ShapedRecipe(new NamespacedKey(plugin, "DragonHelmet"), itemStack[0].clone());
        recipes[0].shape(
                "XXX",
                "X X",
                "   ");
        recipes[0].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[1] = new ShapedRecipe(new NamespacedKey(plugin, "DragonChestplate"), itemStack[1].clone());
        recipes[1].shape(
                "X X",
                "XXX",
                "XXX");
        recipes[1].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[2] = new ShapedRecipe(new NamespacedKey(plugin, "DragonLeggings"), itemStack[2].clone());
        recipes[2].shape(
                "XXX",
                "X X",
                "X X");
        recipes[2].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        recipes[3] = new ShapedRecipe(new NamespacedKey(plugin, "DragonBoots"), itemStack[3].clone());
        recipes[3].shape(
                "X X",
                "X X",
                "   ");
        recipes[3].setIngredient('X', new RecipeChoice.ExactChoice(item.clone()));

        return recipes;
    }

    String[] IDs = {"DragonBoots", "DragonLeggings", "DragonChestplate", "DragonHelmet"};

    @Override
    public void checkAllArmor(Player player, ItemStack[] armors) {
        for (int i = 0; i < 4; i++) {
            boolean pass = false;
            if (armors[i] != null) {
                if (armors[i].hasItemMeta()
                        && armors[i].getItemMeta().getPersistentDataContainer().has(
                        plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME))
                        && armors[i].getItemMeta().getPersistentDataContainer().get(
                        plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME),
                        PersistentDataType.STRING).equals(ARMOR_FAMILY_DRAGON)) {

                    if (plugin.getModifierMain().hasModifier(player, IDs[i])) continue;

                    // 根據部位添加不同的修飾符
                    switch (i) {
                        case 3: // 頭盔 - 爆擊機率
                            if (armors[i].getItemMeta().getPersistentDataContainer().has(
                                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_CRIT_CHANCE_ADD))) {
                                double critChance = armors[i].getItemMeta()
                                        .getPersistentDataContainer().get(
                                                plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_CRIT_CHANCE_ADD),
                                                PersistentDataType.DOUBLE);
                                Modifier modifier = new Modifier(IDs[i], NSKeyRepo.KEY_PD_CRIT_CHANCE, ModifierType.ADD, critChance);
                                plugin.getModifierMain().addModifier(player, modifier);
                                pass = true;
                            }
                            break;
                        case 2: // 胸甲 - 防禦力 (由原版屬性處理)
                            pass = true;
                            break;
                        case 1: // 褲子 - 爆擊傷害
                            if (armors[i].getItemMeta().getPersistentDataContainer().has(
                                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_CRIT_DAMAGE_ADD))) {
                                double critDamage = armors[i].getItemMeta()
                                        .getPersistentDataContainer().get(
                                                plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_CRIT_DAMAGE_ADD),
                                                PersistentDataType.DOUBLE);
                                Modifier modifier = new Modifier(IDs[i], NSKeyRepo.KEY_PD_CRIT_DAMAGE, ModifierType.ADD, critDamage);
                                plugin.getModifierMain().addModifier(player, modifier);
                                pass = true;
                            }
                            break;
                        case 0: // 鞋子 - 移動速度 (由原版屬性處理)
                            pass = true;
                            break;
                    }
                }
            }
            if (!pass) {
                plugin.getModifierMain().removeModifier(player, IDs[i]);
            }
        }
    }

    @Override
    public boolean isFullSet(ItemStack[] armors) {
        for (int i = 0; i < 4; i++) {
            if (armors[i] == null
                    || !armors[i].hasItemMeta()
                    || !armors[i].getItemMeta().getPersistentDataContainer().has(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME))
                    || !armors[i].getItemMeta().getPersistentDataContainer().get(
                    plugin.getNsKeyRepo().getKey(NSKeyRepo.KEY_ARMOR_NAME),
                    PersistentDataType.STRING).equals(ARMOR_FAMILY_DRAGON)) {
                return false;
            }
        }
        return true;
    }

    private static HashMap<UUID, Boolean> dragonFullSet = new HashMap<>();
    //private String fullSetModifierID = "dragonarmor_fullset"; 要りません

    @Override
    public void addFullBouns(NewTMSv8 plugin, Player player) {
        dragonFullSet.put(player.getUniqueId(), true);
        plugin.getLogger().info("dragonFullSet");
    }

    @Override
    public void removeFullBouns(NewTMSv8 plugin, Player player) {
        dragonFullSet.remove(player.getUniqueId());
    }

    // 箭矢自動瞄準事件
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Arrow)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) event.getEntity().getShooter();
        if (!dragonFullSet.containsKey(shooter.getUniqueId())) return;
        plugin.getLogger().info("aiming");
        Arrow arrow = (Arrow) event.getEntity();

        // 啟動箭矢追蹤任務
        new BukkitRunnable() {
            int ticks = 0;
            final int maxTicks = 100; // 5秒後停止追蹤

            @Override
            public void run() {
                if (arrow.isDead() || arrow.isOnGround() || ticks++ > maxTicks) {
                    this.cancel();
                    return;
                }

                // 尋找3格範圍內的敵對實體
                LivingEntity target = null;
                double minDistance = 8.0;

                for (LivingEntity entity : arrow.getWorld().getLivingEntities()) {
                    if (entity == shooter) continue;
                    if (entity.getLocation().distance(arrow.getLocation()) <= minDistance) {
                        target = entity;
                        minDistance = entity.getLocation().distance(arrow.getLocation());
                    }
                }

                if (target != null) {
                    // 計算朝向目標的向量
                    Vector direction = target.getLocation().add(0, 1, 0).subtract(arrow.getLocation()).toVector();
                    direction.normalize();
                    direction.multiply(arrow.getVelocity().length()); // 保持原有速度
                    arrow.setVelocity(direction);
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }
}