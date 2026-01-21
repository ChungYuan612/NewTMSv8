package me.cyperion.ntms.Event.EventDetail;

import me.cyperion.ntms.Event.NTMSEventChangeEvent;
import me.cyperion.ntms.Event.NTMSEvents;
import me.cyperion.ntms.NewTMSv8;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static me.cyperion.ntms.Utils.colors;

public class ScratchManager {

    private final NewTMSv8 plugin;
    private final Map<String, ScratchEntry> scratchMap = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> playerCounts = new ConcurrentHashMap<>();
    private BukkitTask cleanupTask = null;

    public ScratchManager(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        startCleanupTask();
        plugin.getNtmsEvents().signUpEventChangeListener(new NTMSEventChangeEvent() {
            @Override
            public void onEventChange(NTMSEvents.EventType oldEvent, NTMSEvents.EventType newEvent) {
                if(oldEvent== NTMSEvents.EventType.LOTTERY_BONUS_EVENT){
                    // reset player counts when event ends
                    refreshCounts();
                    plugin.getLogger().info("[ScratchManager] 刮刮樂活動結束，已重置玩家記數。");
                }
            }
        });
    }

    public void shutdown() {
        // cancel cleanup task
        if (cleanupTask != null && !cleanupTask.isCancelled()) {
            cleanupTask.cancel();
        }
        scratchMap.clear();
        playerCounts.clear();
    }

    public boolean isEventActive() {
        return plugin.getNtmsEvents().getNowEvent() == NTMSEvents.EventType.LOTTERY_BONUS_EVENT;
    }

    // Create an in-memory scratch card and return its id
    public String createScratch(UUID owner) {
        String id = UUID.randomUUID().toString();
        int prize = pickPrize();
        int threshold = plugin.getScratchConfig().getInt("clicks_to_reveal", 5);
        long ttlSeconds = plugin.getScratchConfig().getLong("scratch_ttl_seconds", 1200L);
        long expireAt = System.currentTimeMillis() + ttlSeconds * 1000L;

        ScratchEntry e = new ScratchEntry();
        e.id = id;
        e.owner = owner;
        e.prize = prize;
        e.clicks = 0;
        e.threshold = threshold;
        e.revealed = false;
        e.createdAt = System.currentTimeMillis();
        e.expireAt = expireAt;

        scratchMap.put(id, e);
        return id;
    }

    private int pickPrize() {
        List<Map<?, ?>> pool = plugin.getScratchConfig().getMapList("prize_pool");
        int total = 0;
        for (Map<?, ?> m : pool) {
            Object w = m.get("weight");
            if (w instanceof Number) total += ((Number) w).intValue();
        }
        if (total <= 0) return 0;
        int r = new Random().nextInt(total) + 1;
        int cum = 0;
        for (Map<?, ?> m : pool) {
            int weight = ((Number) m.get("weight")).intValue();
            cum += weight;
            if (r <= cum) {
                return ((Number) m.get("amount")).intValue();
            }
        }
        return 0;
    }

    // accessors for commands
    public ScratchEntry getScratch(String id) {
        ScratchEntry e = scratchMap.get(id);
        if (e == null) return null;
        // check expiry
        if (System.currentTimeMillis() > e.expireAt) {
            scratchMap.remove(id);
            return null;
        }
        return e;
    }

    public void saveScratch(ScratchEntry e) {
        // in-memory: just put back
        scratchMap.put(e.id, e);
    }

    public void removeScratch(String id) {
        scratchMap.remove(id);
    }

    public void sendClickableScratch(Player p, String id) {
        String text = "§6[刮刮樂] §e點我刮！(多按幾次可揭露)";
        TextComponent tc = new TextComponent(text);
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/scratch_click " + id));
        p.spigot().sendMessage(tc);

    }

    public boolean givePrize(Player p, int amount) {
        if (amount <= 0) return false;
        if (plugin.getEconomy() != null && plugin.getScratchConfig().getBoolean("give_economy", true)) {
            OfflinePlayer of = Bukkit.getOfflinePlayer(p.getUniqueId());
            EconomyResponse r = plugin.getEconomy().depositPlayer(of, amount);
            return r != null && r.transactionSuccess();
        } else {
            // vault missing or disabled: just notify (no actual give)
            return false;
        }
    }

    // player count management (per-event)
    public boolean canCreateScratch(UUID player) {
        int max = plugin.getScratchConfig().getInt("max_per_player_per_event", 3);
        return playerCounts.getOrDefault(player, 0) < max;
    }

    public void incrementPlayerCount(UUID player) {
        playerCounts.put(player, playerCounts.getOrDefault(player, 0) + 1);
    }

    public int getPlayerCount(UUID player) {
        return playerCounts.getOrDefault(player, 0);
    }

    /**
     * refreshCounts:
     * 我在此實作為清空 playerCounts 並回傳 true，活動要重新啟動時呼叫即可。
     */
    public void refreshCounts() {
        playerCounts.clear();
    }

    // cleanup task to remove expired scratches periodically
    private void startCleanupTask() {
        int interval = Math.max(5, plugin.getScratchConfig().getInt("cleanup_interval_seconds", 20));
        cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                int removed = 0;
                Iterator<Map.Entry<String, ScratchEntry>> it = scratchMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, ScratchEntry> en = it.next();
                    ScratchEntry e = en.getValue();
                    if (e.expireAt <= now) {
                        it.remove();
                        removed++;
                    }
                }
                if (removed > 0) {
                    plugin.getLogger().info("Cleaned up " + removed + " expired scratch(es).");
                }
            }
        }.runTaskTimer(plugin, interval * 20L, interval * 20L);
    }
}