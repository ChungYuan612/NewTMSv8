package me.cyperion.ntms.Command;

import me.cyperion.ntms.Event.EventDetail.ScratchManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScratchCommand implements CommandExecutor {

    private final ScratchManager manager;

    public ScratchCommand(ScratchManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage("只能由玩家在遊戲內使用");
            return true;
        }
        Player p = (Player) s;
        if (!manager.isEventActive()) {
            p.sendMessage("§c活動尚未啟動或已結束。");
            return true;
        }

        // 每人上限檢查
        if (!manager.canCreateScratch(p.getUniqueId())) {
            int max = p.getServer().getPluginManager().getPlugin(manager.getClass().getPackage().getName()) == null
                    ? 3 : manager.getPlayerCount(p.getUniqueId()); // fallback, 但我們會直接讀 config in manager
            p.sendMessage("§c每位玩家限刮 " + manager.getPlayerCount(p.getUniqueId()) + " 張（已達上限）。");
            // 更友善的訊息：顯示 config 的上限
            int configuredMax = manager.getPlayerCount(p.getUniqueId());
            p.sendMessage("§c（已達本次活動上限，請等待活動結束或管理員重置）");
            return true;
        }

        // create scratch, increment count, send clickable component
        String id = manager.createScratch(p.getUniqueId());
        manager.incrementPlayerCount(p.getUniqueId());
        manager.sendClickableScratch(p, id);
        p.sendMessage("§a你獲得了一張刮刮樂，請在聊天中點擊上方文字來刮（或重複點擊直到揭露）。");
        return true;
    }
}
