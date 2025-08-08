package me.cyperion.ntms.Event;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 終界龍BOSS戰系統
 * 處理玩家傷害統計、水晶破壞紀錄、排行榜生成和獎勵分配
 */
public class EnderDragonBossSystem implements Listener {

    // 玩家數據統計
    private final Map<UUID, PlayerBossData> playerData = new ConcurrentHashMap<>();
    // 當前BOSS戰狀態
    private EnderDragon currentBoss;
    private boolean bossActive = false;
    private long bossStartTime;

    // BOSS增強配置
    private static final double BOSS_MAX_HEALTH = 1000.0;
    private static final int SKILL_COOLDOWN = 100; // 5秒 (20 ticks/sec)
    private static final int FIREBALL_COOLDOWN = 60; // 3秒

    // 權重配置
    private static final Map<Integer, Integer> RANK_WEIGHTS = Map.of(
            1, 50,  // 第1名
            2, 30,  // 第2名
            3, 20,  // 第3名
            4, 10   // 第4名及以後 (默認值)
    );

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
            return damageDealt + (crystalsDestroyed * 50) + (skillsHit * 10);
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
     * 開始BOSS戰
     */
    public void startBossFight(World world, Location spawnLocation) {
        if (bossActive) {
            return; // 已有BOSS戰進行中
        }

        // 清理舊數據
        playerData.clear();
        bossStartTime = System.currentTimeMillis();
        bossActive = true;

        // 生成增強版終界龍
        currentBoss = spawnEnhancedEnderDragon(world, spawnLocation);

        // 開始BOSS技能循環
        startBossSkillCycle();

        // 廣播開始消息
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");
        Bukkit.broadcastMessage(ChatColor.GOLD + "    🐲 終界龍BOSS戰開始！ 🐲");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "血量: " + ChatColor.RED + (int)BOSS_MAX_HEALTH + "❤");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "===============================");
    }

    /**
     * 生成增強版終界龍
     */
    private EnderDragon spawnEnhancedEnderDragon(World world, Location location) {
        EnderDragon dragon = (EnderDragon) world.spawnEntity(location, EntityType.ENDER_DRAGON);

        // 設置增強屬性
        dragon.setMaxHealth(BOSS_MAX_HEALTH);
        dragon.setHealth(BOSS_MAX_HEALTH);
        dragon.setCustomName(ChatColor.DARK_PURPLE + "終界龍");
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

                // 每5秒執行特殊技能
                if (tickCounter % SKILL_COOLDOWN == 0) {
                    executeRandomSkill();
                }

                // 每3秒發射火球
                if (tickCounter % FIREBALL_COOLDOWN == 0) {
                    fireballAttack();
                }

                // 更新血量顯示
                updateBossHealthDisplay();
            }
        }.runTaskTimer(getPlugin(), 0L, 1L);
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

        Bukkit.broadcastMessage(ChatColor.YELLOW + "⚡ 終界龍使用了閃電打擊！");
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

        Bukkit.broadcastMessage(ChatColor.GREEN + "☠ 終界龍釋放了毒雲！");
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

        Bukkit.broadcastMessage(ChatColor.GRAY + "💨 終界龍使用了風暴衝擊！");
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

        Bukkit.broadcastMessage(ChatColor.RED + "❤ 終界龍回復了生命值！");
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

    /**
     * 更新BOSS血量顯示
     */
    private void updateBossHealthDisplay() {
        if (currentBoss == null || currentBoss.isDead()) return;

        double healthPercentage = (currentBoss.getHealth() / BOSS_MAX_HEALTH) * 100;
        String healthBar = createHealthBar(healthPercentage);

        String displayName = String.format("%s %s %.0f❤ (%.1f%%)",
                ChatColor.DARK_PURPLE + "增強終界龍",
                healthBar,
                currentBoss.getHealth(),
                healthPercentage
        );

        currentBoss.setCustomName(displayName);
    }

    /**
     * 創建血量條
     */
    private String createHealthBar(double percentage) {
        int bars = 20;
        int filledBars = (int) (percentage / 100.0 * bars);

        StringBuilder healthBar = new StringBuilder();
        healthBar.append(ChatColor.GREEN);

        for (int i = 0; i < bars; i++) {
            if (i < filledBars) {
                if (percentage > 60) healthBar.append(ChatColor.GREEN);
                else if (percentage > 30) healthBar.append(ChatColor.YELLOW);
                else healthBar.append(ChatColor.RED);
                healthBar.append("█");
            } else {
                healthBar.append(ChatColor.GRAY).append("█");
            }
        }

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

        // 判斷傷害來源
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null) {
            PlayerBossData data = getPlayerData(damager.getUniqueId());
            data.addDamage(event.getFinalDamage());

            // 顯示傷害數字
            damager.sendMessage(String.format("%s -%s%.1f ❤",
                    ChatColor.RED, ChatColor.BOLD, event.getFinalDamage()));
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
     * 處理水晶破壞事件
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!bossActive) return;
        if (event.getBlock().getType() != Material.BEDROCK) return; // 假設水晶是基岩

        // 檢查是否為終界水晶附近的方塊
        Location blockLoc = event.getBlock().getLocation();
        for (Entity entity : blockLoc.getWorld().getNearbyEntities(blockLoc, 2, 2, 2)) {
            if (entity instanceof EnderCrystal) {
                PlayerBossData data = getPlayerData(event.getPlayer().getUniqueId());
                data.addCrystalDestroyed();

                event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "🔮 你破壞了終界水晶！ (+50分)");
                break;
            }
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
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "         🐲 BOSS戰結果 🐲");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "戰鬥時間: " + minutes + "分" + seconds + "秒");
        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");

        for (int i = 0; i < Math.min(leaderboard.size(), 10); i++) {
            LeaderboardEntry entry = leaderboard.get(i);
            String rankSymbol = getRankSymbol(entry.getRank());

            Bukkit.broadcastMessage(String.format(
                    "%s #%d %s %s - %.1f分 (傷害: %.1f, 水晶: %d) [權重: %d]",
                    rankSymbol,
                    entry.getRank(),
                    ChatColor.WHITE,
                    entry.getPlayerName(),
                    entry.getData().getTotalScore(),
                    entry.getData().getDamageDealt(),
                    entry.getData().getCrystalsDestroyed(),
                    entry.getWeight()
            ));
        }

        Bukkit.broadcastMessage(ChatColor.GOLD + "========================================");
    }

    /**
     * 獲取排名符號
     */
    private String getRankSymbol(int rank) {
        switch (rank) {
            case 1: return ChatColor.GOLD + "👑";
            case 2: return ChatColor.GRAY + "🥈";
            case 3: return ChatColor.YELLOW + "🥉";
            default: return ChatColor.WHITE + "🏅";
        }
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
        // EconomyAPI.getInstance().addMoney(player, coins);

        // 給予物品獎勵
        List<ItemStack> rewards = new ArrayList<>();

        // 基礎獎勵
        rewards.add(new ItemStack(Material.DIAMOND, weight / 10 + 1));
        rewards.add(new ItemStack(Material.EXPERIENCE_BOTTLE, weight / 5));

        // 排名獎勵
        if (rank == 1) {
            rewards.add(createCustomItem(Material.DRAGON_HEAD, "§6終界龍征服者", "§7擊敗增強終界龍的證明"));
            rewards.add(new ItemStack(Material.NETHERITE_INGOT, 2));
        } else if (rank == 2) {
            rewards.add(new ItemStack(Material.NETHERITE_INGOT, 1));
        } else if (rank == 3) {
            rewards.add(new ItemStack(Material.NETHERITE_SCRAP, 2));
        }

        // 發送獎勵到背包或郵件系統
        for (ItemStack reward : rewards) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(reward);
            } else {
                // 如果背包滿了，可以發送到郵件系統或掉落在地上
                player.getWorld().dropItemNaturally(player.getLocation(), reward);
            }
        }

        player.sendMessage(String.format("%s🎁 你獲得了 %d 金幣和多項獎勵！",
                ChatColor.GREEN, coins));

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

        /* 範例實現：
        switch (rank) {
            case 1:
                // 給予特殊稱號
                // TitleAPI.setTitle(player, "終界龍殺手");
                break;
            case 2:
                // 給予技能點數
                // SkillAPI.addSkillPoints(player, 50);
                break;
            case 3:
                // 給予經驗值
                // player.giveExp(1000);
                break;
        }
        */
    }

    /**
     * 獲取或創建玩家數據
     */
    private PlayerBossData getPlayerData(UUID playerId) {
        return playerData.computeIfAbsent(playerId, k -> new PlayerBossData());
    }

    /**
     * 獲取插件實例 (需要根據你的主類調整)
     */
    private org.bukkit.plugin.Plugin getPlugin() {
        // 返回你的主插件實例
        return org.bukkit.Bukkit.getPluginManager().getPlugin("YourPluginName");
    }

    // Getters
    public boolean isBossActive() { return bossActive; }
    public EnderDragon getCurrentBoss() { return currentBoss; }
    public Map<UUID, PlayerBossData> getPlayerData() { return new HashMap<>(playerData); }
}
