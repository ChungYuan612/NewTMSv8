package me.cyperion.ntms.Event;

import me.cyperion.ntms.ItemStacks.Armors.DragonArmor;
import me.cyperion.ntms.ItemStacks.Item.Emerald_Coins;
import me.cyperion.ntms.Monster.RewardItem;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Utils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.cyperion.ntms.Utils.colors;

/**
 * 終界龍BOSS戰系統
 * 處理玩家傷害統計、水晶破壞紀錄、排行榜生成和獎勵分配
 * 支持終界龍重生自動開始BOSS戰，數據持久化存儲
 */
public class EnderDragonBossSystem implements Listener {

    // 玩家數據統計
    private final Map<UUID, PlayerBossData> playerData = new ConcurrentHashMap<>();
    // 當前BOSS戰狀態
    private EnderDragon currentBoss;
    private UUID currentBossUUID; // 用於持久化識別BOSS
    private boolean bossActive = false;
    private long bossStartTime;

    // 數據文件
    private final File dataFolder;
    private final File bossDataFile;
    private final File playerDataFile;

    // 主插件實例
    private final NewTMSv8 plugin;

    //物品類別
    private DragonArmor dragonArmor;
    private me.cyperion.ntms.ItemStacks.Item.EnderCrystal enderCrystal;

    // BOSS增強配置
    private static final double BOSS_MAX_HEALTH = 800.0;
    private static final int SKILL_COOLDOWN = 200; // 10秒 (20 ticks/sec)
    private static final int FIREBALL_COOLDOWN = 80; // 4秒

    // 本島範圍限制 (-300 到 300)
    private static final int MAIN_ISLAND_LIMIT = 300;

    // 權重配置
    private static final Map<Integer, Integer> RANK_WEIGHTS = Map.of(
            1, 40,  // 第1名
            2, 36,  // 第2名
            3, 28,  // 第3名
            4, 14   // 第4名及以後 (默認值)
    );

    /**
     * 構造函數
     */
    public EnderDragonBossSystem(NewTMSv8 plugin) {
        this.plugin = plugin;
        dragonArmor = new DragonArmor(plugin);
        enderCrystal = new me.cyperion.ntms.ItemStacks.Item.EnderCrystal(plugin);
        this.dataFolder = new File(plugin.getDataFolder(), "boss_data");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.bossDataFile = new File(dataFolder, "boss_fight.yml");
        this.playerDataFile = new File(dataFolder, "player_data.yml");

        // 加載持久化數據
        loadBossData();

        // 檢查是否有正在進行的BOSS戰
        checkForExistingBoss();
    }

    /**
     * 玩家BOSS戰數據類別
     */
    public static class PlayerBossData {
        private double damageDealt = 0.0;
        private int crystalsDestroyed = 0;
        private int skillsHit = 0;
        private long survivalTime = 0;

        // Getters and Setters
        public double getDamageDealt() { return damageDealt; }
        public void addDamage(double damage) { this.damageDealt += damage; }

        public int getCrystalsDestroyed() { return crystalsDestroyed; }
        public void addCrystalDestroyed() { this.crystalsDestroyed++; }

        public int getSkillsHit() { return skillsHit; }
        public void addSkillHit() { this.skillsHit++; }

        public long getSurvivalTime() { return survivalTime; }
        public void setSurvivalTime(long time) { this.survivalTime = time; }

        // 計算總分數 (用於排名)
        public double getTotalScore() {
            return damageDealt + (crystalsDestroyed * 30) + (skillsHit * 10);
        }
    }

    /**
     * 排行榜條目
     */
    public static class LeaderboardEntry {
        private final UUID playerId;
        private final String playerName;
        private final PlayerBossData data;
        private final int rank;
        private final int weight;

        public LeaderboardEntry(UUID playerId, String playerName, PlayerBossData data, int rank, int weight) {
            this.playerId = playerId;
            this.playerName = playerName;
            this.data = data;
            this.rank = rank;
            this.weight = weight;
        }

        // Getters
        public UUID getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public PlayerBossData getData() { return data; }
        public int getRank() { return rank; }
        public int getWeight() { return weight; }
    }

    /**
     * 檢查是否有現存的BOSS龍
     */
    private void checkForExistingBoss() {
        if (currentBossUUID == null) return;

        // 延遲檢查，等待世界完全加載
        new BukkitRunnable() {
            @Override
            public void run() {
                World theEnd = Bukkit.getWorld("world_the_end");
                if (theEnd == null) {
                    theEnd = Bukkit.getWorld("DIM1"); // 有些服務器使用這個名稱
                }

                if (theEnd != null) {
                    // 尋找現有的BOSS龍
                    for (Entity entity : theEnd.getEntities()) {
                        if (entity instanceof EnderDragon && entity.getUniqueId().equals(currentBossUUID)) {
                            currentBoss = (EnderDragon) entity;
                            resumeBossFight();
                            return;
                        }
                    }
                }

                // 如果沒找到BOSS龍，清理數據
                currentBossUUID = null;
                bossActive = false;
                saveBossData();
            }
        }.runTaskLater(plugin, 60L); // 3秒後檢查
    }

    /**
     * 恢復BOSS戰
     */
    private void resumeBossFight() {
        if (currentBoss == null || currentBoss.isDead()) {
            return;
        }

        bossActive = true;

        // 確保BOSS屬性正確
        currentBoss .setMaxHealth(BOSS_MAX_HEALTH);
        currentBoss.setCustomName(ChatColor.DARK_PURPLE + "增強終界龍");
        currentBoss.setCustomNameVisible(true);

        // 重新開始技能循環
        startBossSkillCycle();

        Bukkit.broadcastMessage(ChatColor.GOLD + "===============================");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "    🐲 BOSS戰已恢復！ 🐲");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "繼續戰鬥，數據已保留！");
        Bukkit.broadcastMessage(ChatColor.GOLD + "===============================");
    }

    /**
     * 處理終界龍重生事件
     */
    @EventHandler
    public void onEnderDragonUnknownRespawn(EnderDragonChangePhaseEvent event) {
        // 檢查是否為重生階段
        if (event.getNewPhase() == EnderDragon.Phase.LEAVE_PORTAL) {
            // 延遲檢查重生
            new BukkitRunnable() {
                @Override
                public void run() {
                    checkForDragonRespawn(event.getEntity().getWorld());
                }
            }.runTaskLater(plugin, 100L); // 5秒後檢查
        }
    }
    /**
     * 處理終界龍重生事件
     */
    @EventHandler
    public void onEnderDragonRespawn(EntitySpawnEvent event) {
        // 檢查是否為重生階段
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            // 延遲檢查重生
            new BukkitRunnable() {
                @Override
                public void run() {
                    checkForDragonRespawn(event.getEntity().getWorld());
                }
            }.runTaskLater(plugin, 20L); // 1秒後檢查
        }
    }


    /**
     * 檢查終界龍重生
     */
    private void checkForDragonRespawn(World world) {
        if (bossActive) return; // 已經有BOSS戰進行中

        // 尋找新生成的終界龍
        for (Entity entity : world.getEntities()) {
            if (entity instanceof EnderDragon) {
                EnderDragon dragon = (EnderDragon) entity;

                // 檢查是否在本島範圍內
                Location loc = dragon.getLocation();
                if (isInMainIsland(loc)) {
                    startBossFightWithExistingDragon(dragon);
                    return;
                }
            }
        }
    }

    /**
     * 使用現有終界龍開始BOSS戰
     */
    private void startBossFightWithExistingDragon(EnderDragon dragon) {
        // 清理舊數據
        playerData.clear();
        bossStartTime = System.currentTimeMillis();
        bossActive = true;

        // 設置當前BOSS
        currentBoss = dragon;
        currentBossUUID = dragon.getUniqueId();

        // 增強終界龍
        enhanceEnderDragon(dragon);

        // 開始BOSS技能循環
        startBossSkillCycle();

        // 保存數據
        saveBossData();

        // 廣播開始消息
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");
        Bukkit.broadcastMessage(ChatColor.GOLD + StringUtils.center("    🐲 終界龍重生！BOSS戰開始！ 🐲",40,' '));
        Bukkit.broadcastMessage(ChatColor.YELLOW + StringUtils.center("血量: " + ChatColor.RED + (int)BOSS_MAX_HEALTH + "❤",40,' '));
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");
    }

    /**
     * 增強現有終界龍
     */
    private void enhanceEnderDragon(EnderDragon dragon) {
        dragon.setMaxHealth(BOSS_MAX_HEALTH);
        dragon.setHealth(BOSS_MAX_HEALTH);
        dragon.setCustomName(ChatColor.DARK_PURPLE + "終界龍");
        dragon.setCustomNameVisible(true);

        // 設置BOSS龍階段為戰鬥狀態
        dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
    }

    /**
     * 檢查是否在本島範圍內
     */
    private boolean isInMainIsland(Location location) {
        double x = location.getX();
        double z = location.getZ();
        return Math.abs(x) <= MAIN_ISLAND_LIMIT && Math.abs(z) <= MAIN_ISLAND_LIMIT;
    }

    /**
     * 加載BOSS數據
     */
    private void loadBossData() {
        if (!bossDataFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(bossDataFile);

        bossActive = config.getBoolean("boss_active", false);
        bossStartTime = config.getLong("boss_start_time", System.currentTimeMillis());

        String bossUUIDString = config.getString("boss_uuid");
        if (bossUUIDString != null && !bossUUIDString.isEmpty()) {
            currentBossUUID = UUID.fromString(bossUUIDString);
        }

        // 加載玩家數據
        loadPlayerData();
    }

    /**
     * 保存BOSS數據
     */
    private void saveBossData() {
        FileConfiguration config = new YamlConfiguration();

        config.set("boss_active", bossActive);
        config.set("boss_start_time", bossStartTime);
        config.set("boss_uuid", currentBossUUID != null ? currentBossUUID.toString() : "");

        try {
            config.save(bossDataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("無法保存BOSS數據: " + e.getMessage());
        }

        // 保存玩家數據
        savePlayerData();
    }

    /**
     * 加載玩家數據
     */
    private void loadPlayerData() {
        if (!playerDataFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

        for (String uuidString : config.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(uuidString);
                PlayerBossData data = new PlayerBossData();

                data.damageDealt = config.getDouble(uuidString + ".damage", 0.0);
                data.crystalsDestroyed = config.getInt(uuidString + ".crystals", 0);
                data.skillsHit = config.getInt(uuidString + ".skills_hit", 0);
                data.survivalTime = config.getLong(uuidString + ".survival_time", 0);

                playerData.put(playerId, data);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("無效的玩家UUID: " + uuidString);
            }
        }
    }

    /**
     * 保存玩家數據
     */
    private void savePlayerData() {
        FileConfiguration config = new YamlConfiguration();

        for (Map.Entry<UUID, PlayerBossData> entry : playerData.entrySet()) {
            String uuidString = entry.getKey().toString();
            PlayerBossData data = entry.getValue();

            config.set(uuidString + ".damage", data.damageDealt);
            config.set(uuidString + ".crystals", data.crystalsDestroyed);
            config.set(uuidString + ".skills_hit", data.skillsHit);
            config.set(uuidString + ".survival_time", data.survivalTime);
        }

        try {
            config.save(playerDataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("無法保存玩家數據: " + e.getMessage());
        }
    }

    /**
     * 定期保存數據
     */
    private void startAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (bossActive) {
                    saveBossData();
                }
            }
        }.runTaskTimerAsynchronously(plugin, 1200L, 1200L); // 每分鐘保存一次
    }

    /**
     * 生成增強版終界龍
     */
    private EnderDragon spawnEnhancedEnderDragon(World world, Location location) {
        EnderDragon dragon = (EnderDragon) world.spawnEntity(location, EntityType.ENDER_DRAGON);

        // 設置增強屬性
        dragon.setMaxHealth(BOSS_MAX_HEALTH);
        dragon.setHealth(BOSS_MAX_HEALTH);
        dragon.setCustomName(ChatColor.DARK_PURPLE + "增強終界龍");
        dragon.setCustomNameVisible(true);

        // 設置BOSS龍階段為戰鬥狀態
        dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);

        return dragon;
    }

    /**
     * 開始BOSS技能循環
     */
    private void startBossSkillCycle() {
        new BukkitRunnable() {
            private int tickCounter = 0;

            @Override
            public void run() {
                if (!bossActive || currentBoss == null || currentBoss.isDead()) {
                    this.cancel();
                    return;
                }

                tickCounter++;

                // 每9秒執行特殊技能
                if (tickCounter % SKILL_COOLDOWN == 0) {
                    executeRandomSkill();
                }

                // 每4秒發射火球
                if (tickCounter % FIREBALL_COOLDOWN == 0) {
                    fireballAttack();
                }

                // 更新血量顯示
                updateBossHealthDisplay();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * 執行隨機技能
     */
    private void executeRandomSkill() {
        Random random = new Random();
        int skill = random.nextInt(4);

        switch (skill) {
            case 0:
                lightningStrike();
                break;
            case 1:
                poisonCloud();
                break;
            case 2:
                windBlast();
                break;
            case 3:
                healingAbsorption();
                break;
        }
    }

    /**
     * 技能1: 閃電打擊
     */
    private void lightningStrike() {
        Collection<? extends Player> players = currentBoss.getWorld().getPlayers();
        if (players.isEmpty()) return;

        Player target = (Player) players.toArray()[new Random().nextInt(players.size())];
        Location strikeLocation = target.getLocation();

        currentBoss.getWorld().strikeLightning(strikeLocation);

        // 給附近玩家傷害
        for (Entity entity : strikeLocation.getWorld().getNearbyEntities(strikeLocation, 3, 3, 3)) {
            if (entity instanceof Player) {
                ((Player) entity).damage(8.0);
                getPlayerData(entity.getUniqueId()).addSkillHit();
            }
        }

        sendMessage(ChatColor.YELLOW + "⚡ 終界龍使用了閃電打擊！");
    }

    /**
     * 技能2: 毒雲
     */
    private void poisonCloud() {
        Location center = currentBoss.getLocation().clone().subtract(0, 10, 0);

        // 創建毒雲效果
        for (int i = 0; i < 20; i++) {
            Location particleLocation = center.clone().add(
                    (Math.random() - 0.5) * 10,
                    Math.random() * 3,
                    (Math.random() - 0.5) * 10
            );
            currentBoss.getWorld().spawnParticle(Particle.WITCH, particleLocation, 10);
        }

        // 對範圍內玩家施加中毒效果
        for (Entity entity : center.getWorld().getNearbyEntities(center, 8, 5, 8)) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                        org.bukkit.potion.PotionEffectType.POISON, 100, 1));
                getPlayerData(player.getUniqueId()).addSkillHit();
            }
        }

        sendMessage(ChatColor.GREEN + "☠ 終界龍釋放了毒雲！");
    }

    /**
     * 技能3: 風暴衝擊
     */
    private void windBlast() {
        Location center = currentBoss.getLocation();

        for (Entity entity : center.getWorld().getNearbyEntities(center, 15, 15, 15)) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                Vector knockback = player.getLocation().toVector().subtract(center.toVector()).normalize().multiply(2);
                player.setVelocity(knockback);
                player.damage(5.0);
                getPlayerData(player.getUniqueId()).addSkillHit();
            }
        }

        // 特效
        currentBoss.getWorld().spawnParticle(Particle.EXPLOSION, center, 5);
        currentBoss.getWorld().playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);

        sendMessage(ChatColor.GRAY + "💨 終界龍使用了風暴衝擊！");
    }

    /**
     * 技能4: 治療吸收
     */
    private void healingAbsorption() {
        double healAmount = BOSS_MAX_HEALTH * 0.05; // 回復5%血量
        double currentHealth = currentBoss.getHealth();
        double newHealth = Math.min(currentHealth + healAmount, BOSS_MAX_HEALTH);

        currentBoss.setHealth(newHealth);
        currentBoss.getWorld().spawnParticle(Particle.HEART, currentBoss.getLocation(), 20);

        sendMessage(ChatColor.RED + "❤ 終界龍回復了生命值！");
    }

    /**
     * 火球攻擊
     */
    private void fireballAttack() {
        Collection<? extends Player> players = currentBoss.getWorld().getPlayers();
        if (players.isEmpty()) return;

        Player target = (Player) players.toArray()[new Random().nextInt(players.size())];

        Fireball fireball = currentBoss.getWorld().spawn(
                currentBoss.getLocation().add(currentBoss.getLocation().getDirection().multiply(2)),
                Fireball.class
        );

        Vector direction = target.getLocation().subtract(fireball.getLocation()).toVector().normalize();
        fireball.setDirection(direction);
        fireball.setShooter(currentBoss);
    }

    private void sendMessage(String message){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(player.getWorld().getName().equals("world_the_end")){
                player.sendMessage(ChatColor.DARK_PURPLE+"[終界資訊] "+message);
            }
        }
    }

    /**
     * 更新BOSS血量顯示
     */
    private void updateBossHealthDisplay() {
        if (currentBoss == null || currentBoss.isDead()) return;

        double healthPercentage = (currentBoss.getHealth() / BOSS_MAX_HEALTH) * 100;
        String healthBar = createHealthBar(healthPercentage);
        //                                   \%s/
        String displayName = String.format("%s %s%.0f❤ (%.1f%%)",
                ChatColor.DARK_PURPLE + "終界龍",
                healthBar,
                currentBoss.getHealth(),
                healthPercentage
        );

        currentBoss.setCustomName(displayName);
    }

    /**
     * 創建血量條 被我改到剩下顏色而已
     */
    private String createHealthBar(double percentage) {
        StringBuilder healthBar = new StringBuilder();

        if (percentage > 60) healthBar.append(ChatColor.GREEN);
        else if (percentage > 30) healthBar.append(ChatColor.YELLOW);
        else healthBar.append(ChatColor.RED);

        return healthBar.toString();
    }

    /**
     * 處理實體受傷事件
     */
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!bossActive || !(event.getEntity() instanceof EnderDragon)) return;
        if (!event.getEntity().equals(currentBoss)) return;

        Player damager = null;
        boolean isProjectile = false;
        // 判斷傷害來源
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
                isProjectile = true;
            }
        }

        if (damager != null) {
            // 檢查玩家是否在本島範圍內
            if (!isInMainIsland(damager.getLocation())) {
                damager.sendMessage(ChatColor.RED + "⚠ 只有在本島範圍內的傷害才會被計算！");
                return;
            }
            if(isProjectile && enderCrystal.isHoldingThis(damager)){
                event.setDamage(event.getDamage() * 1.2);
            }
            if(event.getDamage() > BOSS_MAX_HEALTH/10){ //每次最多造成10%傷害，防止秒殺
                event.setDamage(BOSS_MAX_HEALTH/10);
            }

            PlayerBossData data = getPlayerData(damager.getUniqueId());
            data.addDamage(event.getFinalDamage());

            // 顯示傷害數字
            //damager.sendMessage(String.format("%s -%s%.1f ❤",
            //        ChatColor.RED, ChatColor.BOLD, event.getFinalDamage()));

            // 定期保存數據
            if (System.currentTimeMillis() % 30000 < 1000) { // 每30秒保存一次
                savePlayerData();
            }
        }
    }

    /**
     * 處理實體死亡事件
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!bossActive || !(event.getEntity() instanceof EnderDragon)) return;
        if (!event.getEntity().equals(currentBoss)) return;

        endBossFight();
    }

    /**
     * 處理水晶破壞事件 棄用
     */
    @Deprecated
    public void onBlockBreak(BlockBreakEvent event) {
        if (!bossActive) return;

        // 檢查玩家是否在本島範圍內
        if (!isInMainIsland(event.getPlayer().getLocation())) {
            return;
        }

        // 檢查是否為終界水晶附近的方塊
        Location blockLoc = event.getBlock().getLocation();
        for (Entity entity : blockLoc.getWorld().getNearbyEntities(blockLoc, 3, 3, 3)) {
            if (entity instanceof EnderCrystal) {
                PlayerBossData data = getPlayerData(event.getPlayer().getUniqueId());
                data.addCrystalDestroyed();

                event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "🔮 你破壞了終界水晶！ (+30分)");
                break;
            }
        }
    }

    /**
     * 處理終界水晶破壞事件
     */
    @EventHandler
    public void onEnderCrystalDamage(EntityDamageByEntityEvent event) {
        if (!bossActive || !(event.getEntity() instanceof EnderCrystal)) return;

        Player damager = null;

        // 判斷破壞來源
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null && isInMainIsland(damager.getLocation())) {
            // 如果這次攻擊會摧毀水晶
            PlayerBossData data = getPlayerData(damager.getUniqueId());
            data.addCrystalDestroyed();

            damager.sendMessage(ChatColor.LIGHT_PURPLE + "🔮 你破壞了終界水晶！ (+30分)");

        }
    }

    /**
     * 結束BOSS戰
     */
    private void endBossFight() {
        bossActive = false;
        long fightDuration = System.currentTimeMillis() - bossStartTime;

        // 更新玩家生存時間
        for (UUID playerId : playerData.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                playerData.get(playerId).setSurvivalTime(fightDuration);
            }
        }

        // 生成排行榜
        List<LeaderboardEntry> leaderboard = generateLeaderboard();

        // 顯示結果
        displayResults(leaderboard, fightDuration);

        // 分配獎勵
        distributeRewards(leaderboard);

        // 清理數據
        currentBoss = null;
        currentBossUUID = null;
        playerData.clear();

        // 刪除保存的數據文件
        if (bossDataFile.exists()) bossDataFile.delete();
        if (playerDataFile.exists()) playerDataFile.delete();
    }

    /**
     * 生成排行榜
     */
    private List<LeaderboardEntry> generateLeaderboard() {
        List<LeaderboardEntry> entries = new ArrayList<>();

        // 按總分數排序
        playerData.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue().getTotalScore(), e1.getValue().getTotalScore()))
                .forEach(entry -> {
                    UUID playerId = entry.getKey();
                    Player player = Bukkit.getPlayer(playerId);
                    String playerName = player != null ? player.getName() : "Unknown";

                    int rank = entries.size() + 1;
                    int weight = RANK_WEIGHTS.getOrDefault(rank, 10);
                    weight += (int) (playerData.get(playerId).getTotalScore()/100.0);
                    //計算weight = Rank + score/100
                    entries.add(new LeaderboardEntry(playerId, playerName, entry.getValue(), rank, weight));
                });

        return entries;
    }

    /**
     * 顯示戰鬥結果
     */
    private void displayResults(List<LeaderboardEntry> leaderboard, long fightDuration) {
        long minutes = fightDuration / 60000;
        long seconds = (fightDuration % 60000) / 1000;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + StringUtils.center("🐲 BOSS戰結果 🐲",40,' '));
        Bukkit.broadcastMessage(ChatColor.YELLOW + StringUtils.center("戰鬥時間: " + minutes + "分" + seconds + "秒",40,' '));
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");

        for (int i = 0; i < Math.min(leaderboard.size(), 10); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            String rankSymbol = getRankSymbol(entry.getRank());
            String color="&7";
            if(plugin.getConfig().contains(entry.playerId.toString()))
                color = plugin.getConfig().getStringList(entry.playerId.toString()).get(2);
            Bukkit.broadcastMessage(colors(String.format(
                    "%s #%d %s %s&f - &6%.1f分 &f(傷害: %.1f, 水晶: %d) &c[權重: %d]",
                    rankSymbol,
                    entry.getRank(),
                    color,
                    entry.getPlayerName(),
                    entry.getData().getTotalScore(),
                    entry.getData().getDamageDealt(),
                    entry.getData().getCrystalsDestroyed(),
                    entry.getWeight()
            )));
        }

        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
    }

    /**
     * 獲取排名符號
     */
    private String getRankSymbol(int rank) {
        return switch (rank) {
            case 1 -> ChatColor.GOLD + "👑";
            case 2 -> ChatColor.WHITE + "🥈";
            case 3 -> ChatColor.YELLOW + "🥉";
            default -> ChatColor.GRAY + "🏅";
        };
    }

    /**
     * 分配獎勵
     */
    private void distributeRewards(List<LeaderboardEntry> leaderboard) {
        for (LeaderboardEntry entry : leaderboard) {
            Player player = Bukkit.getPlayer(entry.getPlayerId());
            if (player != null && player.isOnline()) {
                giveRewardToPlayer(player, entry.getWeight(), entry.getRank());
            }
        }
    }

    /**
     * 給予玩家獎勵
     */
    private void giveRewardToPlayer(Player player, int weight, int rank) {
        // 根據權重計算獎勵
        int coins = weight * 100; // 基礎金錢獎勵

        // 給予金錢 (這裡需要根據你的經濟插件調整)
        plugin.getEconomy().depositPlayer(player, coins);
        // 基礎獎勵
        ItemStack rewards=new ItemStack(Material.EXPERIENCE_BOTTLE, weight / 5);
        Utils.giveItem(player,rewards,weight/5);

        player.sendMessage(colors(String.format("&a🎁 你獲得了 &6%d&a 元獎金還有一些物品！",coins)));
        player.sendMessage(colors(String.format("&f +&9%dx&f經驗瓶！",weight / 5)));

        // 調用自定義獎勵方法
        distributeCustomRewards(player, weight, rank);
    }

    /**
     * 創建自定義物品
     */
    private ItemStack createCustomItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * TODO: 自定義獎勵分配方法
     * 在這裡實現你的特殊獎勵邏輯
     */

    private void distributeCustomRewards(Player player, int weight, int rank) {
        // TODO: 在這裡添加你的自定義獎勵邏輯
        // 例如：
        // - 特殊稱號
        // - 技能點數
        // - 特殊權限
        // - 公會貢獻點
        // - 成就解鎖等等
        // 排名獎勵
        List<ItemStack> rewards = new ArrayList<>();

        rewards.add(new ItemStack(Material.NETHERITE_SCRAP, weight / 10));
        player.sendMessage(colors(String.format("&f +&9%dx&f獄隨碎片！",weight / 10)));

        if (rank == 1) {
            rewards.add(createCustomItem(Material.DRAGON_HEAD, "§6終界龍征服者", "§7擊敗增強終界龍的證明"));

        }
        if(weight >= 40){
            double left = weight - 39;

            for(int i = 0;i<4;i++){
                RewardItem dragonReward = new RewardItem(plugin,dragonArmor.getItemStacks()[i], Math.round(6 + left /4));
                dragonReward.tryDropLoot(player);
            }
        }

        if(weight >= 45){
            RewardItem endCrystalReward = new RewardItem(plugin,enderCrystal.toItemStackFragment(), 10);
            endCrystalReward.tryDropLoot(player);
        }

        RewardItem emerald = new RewardItem(plugin,new Emerald_Coins().toItemStack(),5);
        emerald.tryDropLoot(player);




        for(ItemStack itemStack:rewards){
            Utils.giveItem(player,itemStack,itemStack.getAmount());
        }
    }

    /**
     * 管理員命令：強制結束BOSS戰
     */
    public boolean forceEndBossFight() {
        if (!bossActive) {
            return false;
        }

        Bukkit.broadcastMessage(ChatColor.RED + "管理員強制結束了BOSS戰！");
        endBossFight();
        return true;
    }

    /**
     * 獲取BOSS戰狀態信息
     */
    public String getBossStatus() {
        if (!bossActive) {
            return ChatColor.GRAY + "目前沒有進行中的BOSS戰";
        }

        long duration = System.currentTimeMillis() - bossStartTime;
        long minutes = duration / 60000;
        long seconds = (duration % 60000) / 1000;

        double healthPercentage = currentBoss != null ? (currentBoss.getHealth() / BOSS_MAX_HEALTH) * 100 : 0;

        return String.format("%sBOSS戰進行中！\n%s持續時間: %d分%d秒\n%sBOSS血量: %.1f%%\n%s參與玩家: %d人",
                ChatColor.GOLD,
                ChatColor.YELLOW, minutes, seconds,
                ChatColor.RED, healthPercentage,
                ChatColor.GREEN, playerData.size());
    }

    /**
     * 獲取玩家戰鬥統計
     */
    public String getPlayerStats(Player player) {
        PlayerBossData data = playerData.get(player.getUniqueId());
        if (data == null) {
            return ChatColor.GRAY + "你還沒有參與當前的BOSS戰";
        }

        return String.format("%s%s 的戰鬥統計:\n%s傷害輸出: %.1f\n%s水晶破壞: %d\n%s技能命中: %d\n%s總分: %.1f",
                ChatColor.GOLD, player.getName(),
                ChatColor.RED, data.getDamageDealt(),
                ChatColor.LIGHT_PURPLE, data.getCrystalsDestroyed(),
                ChatColor.YELLOW, data.getSkillsHit(),
                ChatColor.GREEN, data.getTotalScore());
    }

    /**
     * 獲取或創建玩家數據
     */
    private PlayerBossData getPlayerData(UUID playerId) {
        return playerData.computeIfAbsent(playerId, k -> new PlayerBossData());
    }

    /**
     * 手動開始BOSS戰 (用於測試或管理員命令)
     */
    public boolean startBossFight(World world, Location spawnLocation) {
        if (bossActive) {
            return false; // 已有BOSS戰進行中
        }

        // 檢查世界是否為終界
        if (!isEndWorld(world)) {
            return false; // 只能在終界開始BOSS戰
        }

        // 清理舊數據
        playerData.clear();
        bossStartTime = System.currentTimeMillis();
        bossActive = true;

        // 生成增強版終界龍
        currentBoss = spawnEnhancedEnderDragon(world, spawnLocation);
        currentBossUUID = currentBoss.getUniqueId();

        // 開始BOSS技能循環
        startBossSkillCycle();

        // 開始自動保存
        startAutoSave();

        // 保存數據
        saveBossData();

        // 廣播開始消息
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");
        Bukkit.broadcastMessage(ChatColor.GOLD + "    🐲 終界龍BOSS戰開始！ 🐲");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "血量: " + ChatColor.RED + (int)BOSS_MAX_HEALTH + "❤");
        Bukkit.broadcastMessage(ChatColor.GRAY + "只有在本島戰鬥才計算傷害！");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");

        return true;
    }



    /**
     * 初始化系統
     */
    public void initialize() {
        // 開始自動保存
        startAutoSave();

        // 註冊事件監聽器
        Bukkit.getPluginManager().registerEvents(this, plugin);

        plugin.getLogger().info("終界龍BOSS系統已初始化！");
        if (bossActive) {
            plugin.getLogger().info("檢測到進行中的BOSS戰，正在恢復...");
        }
    }

    /**
     * 關閉系統
     */
    public void shutdown() {
        if (bossActive) {
            saveBossData();
            plugin.getLogger().info("BOSS戰數據已保存");
        }
    }
    /**
     * 檢查世界是否為終界
     */
    private boolean isEndWorld(World world) {
        return world.getEnvironment() == World.Environment.THE_END;
    }

    // Getters
    public boolean isBossActive() { return bossActive; }
    public EnderDragon getCurrentBoss() { return currentBoss; }
    public Map<UUID, PlayerBossData> getPlayerData() { return new HashMap<>(playerData); }

}