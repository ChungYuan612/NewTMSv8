package me.cyperion.ntms.Event.EventDetail;

import java.util.UUID;

public class ScratchEntry {
    public String id;
    public UUID owner;
    public int prize;
    public int clicks;
    public int threshold;
    public boolean revealed;
    public long createdAt;
    public long expireAt;
}
