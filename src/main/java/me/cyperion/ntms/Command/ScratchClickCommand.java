package me.cyperion.ntms.Command;

import me.cyperion.ntms.Event.EventDetail.ScratchEntry;
import me.cyperion.ntms.Event.EventDetail.ScratchManager;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScratchClickCommand implements CommandExecutor {

    private final NewTMSv8 plugin;
    private final ScratchManager manager;

    public ScratchClickCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
        this.manager = plugin.getScratchManager();
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage("只能由玩家在遊戲內使用");
            return true;
        }
        Player p = (Player) s;
        if (args.length == 0) {
            p.sendMessage("錯誤的用法。");
            return true;
        }
        String id = args[0];
        ScratchEntry e = manager.getScratch(id);
        if (e == null) {
            p.sendMessage("這張刮刮樂不存在或已失效（可能已過期）。");
            return true;
        }
        if (!e.owner.equals(p.getUniqueId())) {
            p.sendMessage("這張刮刮樂不是你的！");
            return true;
        }
        if (e.revealed) {
            p.sendMessage("這張刮刮樂已被刮開，金額：" + e.prize + " 元");
            return true;
        }
        // increment clicks
        e.clicks += 1;
        manager.saveScratch(e);

        // show progress
        //int remain = Math.max(0, e.threshold - e.clicks);
        //p.sendMessage("§a已刮 " + e.clicks + " / " + e.threshold + " 次，還需 " + remain + " 次。");
        p.playSound(p.getLocation(), Sound.ENTITY_ARMADILLO_BRUSH,1,1);
        if (e.clicks >= e.threshold) {
            e.revealed = true;
            manager.saveScratch(e);
            // reveal
            p.sendMessage("§6恭喜！你刮中了 §e" + e.prize + " 元" + "§6！");
            boolean gave = manager.givePrize(p, e.prize);
            if (gave) {
                if(e.prize >=10000)
                    Bukkit.broadcastMessage("§6[刮刮樂] §e玩家 §b" + p.getName() + "§e 刮中了 " + e.prize + " 元!恭喜恭喜!");
                p.sendMessage("§a已發放至你的帳戶。");
            } else {
                if (plugin.getEconomy() == null) {
                    p.sendMessage("§c發放失敗：伺服器未安裝 Vault 或 Economy provider。請聯絡管理員。");
                } else {
                    p.sendMessage("§e（注意：此伺服器未啟用自動發幣，實際發放請由管理員核發）");
                }
            }
        }

        return true;
    }
}
