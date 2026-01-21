package me.cyperion.ntms.Event;

public interface NTMSEventChangeEvent {
    void onEventChange(NTMSEvents.EventType oldEvent, NTMSEvents.EventType newEvent);
}
