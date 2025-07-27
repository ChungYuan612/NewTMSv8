package me.cyperion.ntms.SideBoard;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static me.cyperion.ntms.Utils.SpecialChar;
import static me.cyperion.ntms.Utils.colors;

/**
 * 管理所有NTMS時間的管理，放在記分板的是別的東西
 * 使用BingAI輔助製作(但是大部分都還是我...)
 */
public class TMWorldTimer {
    private NewTMSv8 plugin;

    private long lastEventTriggerTime = 0;

    public static final String
            WT_YEAR="years",
            WT_MONTH="months",
            WT_DAY="days",
            WT_HOUR="hours",
            WT_MINUTE="minutes";

    public TMWorldTimer(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    private long convertToTime(World world){
        return world.getTime()+6000;
    }
    private int convertTimeToHours(long time){
        return (int) ((time / 1000) % 24);
    }

    private int convertTimeToMinutes(long time){
        return (int) (60 * (time % 1000) / 1000);
    }

    /**
     * 這個方法也會處理NTMSEvent的觸發事項
     */
    private Map<String, Integer> getTime(World world) {
        long time = convertToTime(world);
        int hours = convertTimeToHours(time);
        int minutes = convertTimeToMinutes(time);
        minutes = minutes - minutes % 10;  // Round down to nearest 10 minutes

        if(hours == 6 && minutes == 0 && lastEventTriggerTime + 60000 <= System.currentTimeMillis()){
            triggerEvent();
            plugin.getLogger().info("觸發隨機活動");
            //剩下觸發完後面就各自辦事，這邊不會再做任何步驟，除了刷新記分板
        }

        hours %= 12;
        if(hours == 0)
            hours = 12;
        Map<String, Integer> realTime = new HashMap<>();
        realTime.put(WT_HOUR, hours);
        realTime.put(WT_MINUTE, minutes);



        return realTime;
    }

    /**
     * 觸發活動的方法
     */
    public void triggerEvent(){
        lastEventTriggerTime = System.currentTimeMillis();
        plugin.getNtmsEvents().triggerNewEvent();
        //刷新記分板，還有發送訊息
        for(Player player : Bukkit.getOnlinePlayers()){
            plugin.getTwPlayerSideBoard().refreshEventScoreboard(player);
            player.playSound(player.getLocation(), "random.levelup", 1, 1);
        }
    }


    private String getIcon(World world){
        long time = convertToTime(world);
        int hours = convertTimeToHours(time);
        String icon = SpecialChar("&b☽");
        if ( hours >=6 && hours <18){
            icon = SpecialChar("&e☀");
        }
        return icon;
    }

    private String getAmPm(World world){
        long time = convertToTime(world);
        int hours = convertTimeToHours(time);
        return hours > 11?"pm":"am";
    }

    /**
     * 取得記分板顯示時間的文字(一整行)，參考SkyBlock
     * @param world 通常這個就是台灣的啦
     * @return 記分板顯示時間的文字(已上色)
     */
    public String getHourDisplayString(World world){
        Map<String, Integer> map = getTime(world);
        String display = " "+map.get(WT_HOUR)+":"+String.format("%02d", map.get(WT_MINUTE))+
                getAmPm(world)+" "+getIcon(world);
        // 2:00pm ☀
        return colors(display);
    }

    /**
     * 取得記分板顯示日期的文字(一整行)，包含年分
     * @param world 通常這個就是台灣的啦
     * @return 記分板顯示日期的文字(已上色)
     */
    public String getDateDisplayString(World world){
        Map<String, Integer> d = getDate(world);
        String display = " &f"+d.get(WT_YEAR)+"年"+d.get(WT_MONTH)+"月"+d.get(WT_DAY)+"日";
        return colors(display);
    }

    //判斷是否需要更新日期(接近午夜，我取400個小刻)
    public boolean needChangeDateString(World world){
        long time = convertToTime(world) % 24000;
        if(time <6200 && time > 5800)
            return true;
        return time < 200 || time > 23800;
    }


    public Map<String, Integer> getDate(World world) {
        long days = (world.getFullTime() + 6000) / 24000;  // Shift time by 6000 ticks
        int years = (int) (days / 365)+1;
        days %= 365;

        int[] monthDays = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int month = 0;
        while (days >= monthDays[month]) {
            days -= monthDays[month];
            month++;
        }

        Map<String, Integer> realDate = new HashMap<>();
        realDate.put(WT_YEAR, years);
        realDate.put(WT_MONTH, month + 1);  // Months are 0-indexed, so add 1
        realDate.put(WT_DAY, (int)days + 1);  // Dates are 0-indexed, so add 1

        return realDate;
    }






}
