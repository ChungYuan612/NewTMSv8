package me.cyperion.ntms.SideBoard;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import static me.cyperion.ntms.Utils.colors;

public class TWPlayerSideBoard extends BukkitRunnable implements Listener {
    private final NewTMSv8 plugin;

    final String MONEY_SBTEAM = "MoneyTeam",
            TIMER_DATE_SBTEAM = "TimerDateTeam",
            TIMER_HOUR_SBTEAM = "TimerTeam",
            DATE_SBTEAM = "DateTeam",
            LOCATION_SBTEAM = "locateTeam";

    public TWPlayerSideBoard(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            double player_coins = plugin.getEconomy().getBalance(p);
            Scoreboard scoreboard = p.getScoreboard();
            refreshTimer(p,false);
            scoreboard.getTeam(MONEY_SBTEAM)
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
                colors("&6&l新臺灣地圖 第8季"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Team date_team = scoreboard.registerNewTeam(DATE_SBTEAM);
        date_team.addEntry(colors("&a &f"));
        objective.getScore(colors("&a &f")).setScore(9);

        Score lore_1_null = objective.getScore("   ");
        lore_1_null.setScore(8);

        Team timer1_team = scoreboard.registerNewTeam(TIMER_DATE_SBTEAM);
        timer1_team.addEntry(colors("&b &f"));
        objective.getScore(colors("&b &f")).setScore(7);

        Team timer2_team = scoreboard.registerNewTeam(TIMER_HOUR_SBTEAM);
        timer2_team.addEntry(colors("&c &f"));//RED+WHITE
        objective.getScore(colors("&c &f")).setScore(6);

        Team locate_team = scoreboard.registerNewTeam(LOCATION_SBTEAM);
        locate_team.addEntry(colors("&c &a"));//RED+AQUA
        objective.getScore(colors("&c &a")).setScore(5);

        Score lore_4_null = objective.getScore("  ");
        lore_4_null.setScore(4);

        Team money_team = scoreboard.registerNewTeam(MONEY_SBTEAM);
        money_team.addEntry(colors("&6 &f"));//GOLD+WHITE
        objective.getScore(colors("&6 &f")).setScore(3);//MONEY

        Score lore_6_null = objective.getScore(" ");
        lore_6_null.setScore(2);

        Score lore_7_IP = objective.getScore(colors("&eNTMStore.com"));
        lore_7_IP.setScore(1);

        player.setScoreboard(scoreboard);
        player.getScoreboard()
                .getTeam(DATE_SBTEAM)
                .setPrefix(ChatColor.GRAY+getFormattedDate("MM/dd/yy")
                        +" "+ChatColor.DARK_GRAY+plugin.getConfig().getString("Version"));

    }

    /**
     *  July 9th <br>
     *  10:30pm☽ <br>
     *  如果在地獄或終界，則顯示台灣的基礎時間
     */
    public void refreshTimer(Player player,boolean allUpdate){
        Scoreboard scoreboard = player.getScoreboard();
        World world = player.getWorld();
        if(world.getName().equals("world_nether")
                || world.getName().equals("world_the_end")){
            world = Bukkit.getWorld(plugin.MAIN_WORLD_NAME);
        }
        scoreboard.getTeam(TIMER_HOUR_SBTEAM)
                .setPrefix(plugin.getTmWorldTimer().getHourDisplayString(world));
        //有需要更新日期，就更新
        if(plugin.getTmWorldTimer().needChangeDateString(world) || allUpdate){
            scoreboard.getTeam(TIMER_DATE_SBTEAM)
                    .setPrefix(plugin.getTmWorldTimer().getDateDisplayString(world));
        }
    }

    //更新玩家的所在世界顯示
    public void refreshTimerLocation(Player player){
        Scoreboard scoreboard = player.getScoreboard();
        World world = player.getWorld();
        String worldText="未知世界";
        if(world.getName().equals("world")){
            worldText = "&d台灣地圖";
        }else if(world.getName().equals("world_nether")){
            worldText = "&4地獄";
        }else if(world.getName().equals("world_the_end")){
            worldText = "&7終界";
        }else if(world.getName().equals("resource")){
            worldText = "&a資源世界";
        }
        scoreboard.getTeam(LOCATION_SBTEAM)
                .setPrefix(colors(" &7⏣ &r" +worldText));
    }

    private String getFormattedDate(String format) {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat(format);
        return ft.format(date);
    }


}
