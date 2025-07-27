package me.cyperion.ntms.SideBoard;

import java.util.Random;

/**
 * 台灣伺服器內所有活動，但是控制的部分在時間(TMWorldTimer.java)
 */
public class NTMSEvents {

    private Random random = new Random();

    private EventType nowEvent = EventType.NO_EVENT;
    private int eventTriggerChance = 30;

    public void triggerNewEvent(){
        if(random.nextInt(100) > eventTriggerChance) {
            this.nowEvent = EventType.NO_EVENT;
            return;
        }
        this.nowEvent = EventType.values()[random.nextInt(EventType.values().length-1)+1];
    }

    public EventType getNowEvent(){
        return nowEvent;
    }

    public boolean hasEvent(){
        return nowEvent != EventType.NO_EVENT;
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
