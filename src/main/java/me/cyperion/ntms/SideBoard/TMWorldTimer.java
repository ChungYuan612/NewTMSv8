package me.cyperion.ntms.SideBoard;

import me.cyperion.ntms.NewTMSv7;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用BingAI輔助製作
 */
public class TMWorldTimer {
    private NewTMSv7 plugin;

    private int hour,minute;

    public TMWorldTimer(NewTMSv7 plugin) {
        this.plugin = plugin;
    }

    public Map<String, Integer> getRealTime(World world) {
        long time = world.getTime()+6000;
        int hours = (int) ((time / 1000 + 6) % 24);
        int minutes = (int) (60 * (time % 1000) / 1000);
        minutes = minutes - minutes % 10;  // Round down to nearest 10 minutes

        Map<String, Integer> realTime = new HashMap<>();
        realTime.put("hours", hours);
        realTime.put("minutes", minutes);

        return realTime;
    }


    public Map<String, Integer> getRealDate(World world) {
        long days = (world.getFullTime() + 6000) / 24000;  // Shift time by 6000 ticks
        int years = (int) (days / 365);
        days %= 365;

        int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int month = 0;
        while (days >= monthDays[month]) {
            days -= monthDays[month];
            month++;
        }

        Map<String, Integer> realDate = new HashMap<>();
        realDate.put("years", years);
        realDate.put("months", month + 1);  // Months are 0-indexed, so add 1
        realDate.put("date", (int)days + 1);  // Dates are 0-indexed, so add 1

        return realDate;
    }




}
