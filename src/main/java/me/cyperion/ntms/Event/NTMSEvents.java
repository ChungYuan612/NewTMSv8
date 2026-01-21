package me.cyperion.ntms.Event;

import me.cyperion.ntms.NewTMSv8;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 台灣伺服器內所有活動，但是控制的部分在時間(TMWorldTimer.java)
 */
public class NTMSEvents {

    private NewTMSv8 plugin;

    public NTMSEvents(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    private Random random = new Random();
    private List<NTMSEventChangeEvent> listeners = new ArrayList<>();

    private EventType nowEvent = EventType.NO_EVENT;
    private int eventTriggerChance = 45;

    public void triggerNewEvent(){
        EventType previousEvent = this.nowEvent;

        if(random.nextInt(100) > eventTriggerChance) {
            this.nowEvent = EventType.NO_EVENT;
            return;
        }
        this.nowEvent = EventType.values()[random.nextInt(EventType.values().length-1)+1];
        for(NTMSEventChangeEvent listener : listeners){
            listener.onEventChange(previousEvent, this.nowEvent);
        }
    }

    public EventType getNowEvent(){
        return nowEvent;
    }

    public boolean hasEvent(){
        return nowEvent != EventType.NO_EVENT;
    }

    public void signUpEventChangeListener(NTMSEventChangeEvent listener){
        listeners.add(listener);
        plugin.getLogger().info("[NTMSEvents] 已註冊活動變更監聽器: "+listener.toString());
    }


    public enum EventType{
        NO_EVENT(
                "&7沒有活動",
                "&f今天沒有什麼活動，生活一切正常。"
        ),
        FISHING_BONUS_EVENT(
                "&3釣魚祭",
                "&f拿起你的釣竿，準備好接受大自然的饋贈吧!現在&b釣魚&f享有&3x1.5&f倍的機率釣出寶藏!"
        ),
        RADI_BONUS_EVENT(
                "&c突襲警報",
                "&f拿起你的武器，準備好接受掠奪者的襲擊吧!現在&d突襲&f享有&3x2.5&f倍的獎金!"
        ),
        LOTTERY_BONUS_EVENT(
                "&e全民樂透",
                        "&f輸入&3/scratch&f領取你的專屬刮刮樂吧!每人有&a3&f次機會刮出豐厚獎金!"
        );
        String name, description;

        EventType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

}
