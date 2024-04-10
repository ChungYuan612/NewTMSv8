package me.cyperion.ntms.SideBoard;

import me.cyperion.ntms.NewTMSv7;
import me.cyperion.ntms.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import static me.cyperion.ntms.Utils.colors;

public class TWPlayerSideBoard extends BukkitRunnable {
    private NewTMSv7 plugin;
    private TMWorldTimer timerClass;

    final String MONEY_SBTEAM = "moneyTeam",
            TIMER_DATE_SBTEAM = "moneyTeam",
            TIMER_HOUR_SBTEAM = "moneyTeam",
            DATE_SBTEAM = "moneyTeam";

    public TWPlayerSideBoard(NewTMSv7 plugin) {
        this.plugin = plugin;
        timerClass = new TMWorldTimer(plugin);
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            PersistentDataContainer data = p.getPersistentDataContainer();
            float player_coins = plugin.getDatabase().getMoney(p);
            Scoreboard scoreboard = p.getScoreboard();
            String HourTimerString
                    = "";//TODO
            scoreboard.getTeam("timerHour_team")
                    .setPrefix(ChatColor.GRAY + timer2_setup(Bukkit.getServer().getWorld("world").getTime(), p) + colors('&', DandN));
            scoreboard.getTeam("money_team")
                    .setPrefix(ChatColor.WHITE + "現金: " + ChatColor.GOLD + player_coins);
        }
    }

    //給玩家剛進入伺服器生成一個
    //參考第5季台灣地圖
    public void createPlayerBoard(Player player){

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective(
                "NTMScoreBoard",
                Criteria.DUMMY,
                colors("&6&l新臺灣地圖 第7季"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team date_team = scoreboard.registerNewTeam(DATE_SBTEAM);
        date_team.addEntry(colors("&a &f"));
        objective.getScore(colors("&a &f")).setScore(8);

        Score lore_1_null = objective.getScore("   ");
        lore_1_null.setScore(7);

        Team timer1_team = scoreboard.registerNewTeam(TIMER_DATE_SBTEAM);
        timer1_team.addEntry(colors("&b &f"));
        objective.getScore(colors("&b &f")).setScore(6);

        Team timer2_team = scoreboard.registerNewTeam(TIMER_HOUR_SBTEAM);
        timer2_team.addEntry(colors("&c &f"));//RED+WHITE
        objective.getScore(colors("&c &f")).setScore(5);

        Score lore_4_null = objective.getScore("  ");
        lore_4_null.setScore(4);

        Team money_team = scoreboard.registerNewTeam(MONEY_SBTEAM);
        money_team.addEntry(colors("&6 &f"));//GOLD+WHITE
        objective.getScore(colors("&6 &f")).setScore(3);//MONEY

        Score lore_6_null = objective.getScore(" ");
        lore_6_null.setScore(2);

        Score lore_7_IP = objective.getScore(colors("&e"+plugin.getServer().getIp()));
        lore_7_IP.setScore(1);

        player.setScoreboard(scoreboard);
        player.getScoreboard()
                .getTeam(DATE_SBTEAM)
                .setPrefix(ChatColor.GRAY+getFormattedDate("MM/dd/yy")
                        +" "+ChatColor.DARK_GRAY+plugin.getConfig().getString("Version"));

    }

    public void refreshTimer(Player player){

    }

    private String getFormattedDate(String format) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat(format);
        return ft.format(date);
    }


}
