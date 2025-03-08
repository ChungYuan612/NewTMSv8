package me.cyperion.ntms.Monster;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

public enum Creature {

    LOG_ZOMBIES(
            50.0,
            0.2,
            EntityType.ZOMBIE,
            createItemArmors(
                    createSimpleItemStack(Material.OAK_LOG),
                    createSimpleItemStack(Material.LEATHER_CHESTPLATE,true),
                    createSimpleItemStack(Material.LEATHER_LEGGINGS,true),
                    createSimpleItemStack(Material.LEATHER_BOOTS,true)
            ),new float[] {
            0,0,0,0
            }, createSimpleItemStack(Material.SALMON),
            colors("&b原木求魚"),
            10,
            new LootItem(createSimpleItemStack(Material.OAK_LOG),8,32,100)
    ),ILLUSIONER(
            70.0,
            0.4,
            EntityType.ILLUSIONER,
            createItemArmors(
                    null,
                    null,
                    null,
                    null
            ),new float[] {
            0,0,0,0
    }, createSimpleItemStack(Material.BOW),
            colors(" "),
            20,
            new LootItem(createSimpleItemStack(Material.EMERALD),2,10,90)
    );

    private final double maxHealth;
    private final List<LootItem> listLoot;
    private final double spawnChance;
    private final EntityType entityType;
    private final ItemStack[] armors;
    private final float[] armorsDropChance;
    private final ItemStack mainHandItem;
    private final int experience;
    private final String displayName;

    Creature(
            double maxHealth,
            double spawnChance,
            EntityType entityType,
            ItemStack[] armors,
            float[] armorsDropChance,
            ItemStack mainHandItem,
            String displayName,
            int experience,
            LootItem... loots
    ) {
        this.maxHealth = maxHealth;
        this.listLoot = Arrays.asList(loots);
        this.spawnChance = spawnChance;
        this.entityType = entityType;
        this.armors = armors;
        this.armorsDropChance = armorsDropChance;
        this.mainHandItem = mainHandItem;
        this.displayName = displayName;
        this.experience = experience;
    }

    Creature(
            double maxHealth,
            double spawnChance,
            EntityType entityType,
            ItemStack[] armors,
            float[] armorsDropChance,
            ItemStack mainHandItem,
            String displayName,
            int experience,
            List<LootItem> listLoot
    ) {
        this.maxHealth = maxHealth;
        this.listLoot = listLoot;
        this.spawnChance = spawnChance;
        this.entityType = entityType;
        this.armors = armors;
        this.armorsDropChance = armorsDropChance;
        this.mainHandItem = mainHandItem;
        this.displayName = displayName;
        this.experience = experience;
    }

    public LivingEntity spawn(Location location) {
        LivingEntity mob = (LivingEntity) location.getWorld().spawnEntity(location,entityType);
        mob.setCustomNameVisible(true);
        mob.setCustomName(displayName);
        mob.getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHealth);
        mob.setHealth(maxHealth);
        EntityEquipment equipment = mob.getEquipment();
        if(armors != null)
            equipment.setArmorContents(armors);
        equipment.setHelmetDropChance(armorsDropChance[0]);
        equipment.setChestplateDropChance(armorsDropChance[1]);
        equipment.setLeggingsDropChance(armorsDropChance[2]);
        equipment.setBootsDropChance(armorsDropChance[3]);
        equipment.setItemInMainHand(mainHandItem);
        equipment.setItemInMainHandDropChance(0f);

        return mob;
    }

    public void tryLootItem(Location location){
        for (LootItem lootItem:listLoot) {
            lootItem.tryDropLoot(location);
        }
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<LootItem> getListLoot() {
        return listLoot;
    }

    public ItemStack[] getArmors() {
        return armors;
    }

    public float[] getArmorsDropChance() {
        return armorsDropChance;
    }

    public ItemStack getMainHandItem() {
        return mainHandItem;
    }

    public int getExperience() {
        return experience;
    }

    public static ItemStack createSimpleItemStack(Material itemType,int red,int green,int blue) {
        ItemStack item = new ItemStack(itemType);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(red, green, blue));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSimpleItemStack(Material itemType, boolean isUnbreakable) {
        ItemStack item = new ItemStack(itemType);
        ItemMeta meta = item.getItemMeta();
        meta.setUnbreakable(isUnbreakable);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createSimpleItemStack(Material itemType) {
        return new ItemStack(itemType);
    }


    public static ItemStack[] createItemArmors(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        ItemStack[] armors = new ItemStack[4];
        armors[3] = helmet;
        armors[2] = chestplate;
        armors[1] = leggings;
        armors[0] = boots;
        return armors;
    }
}