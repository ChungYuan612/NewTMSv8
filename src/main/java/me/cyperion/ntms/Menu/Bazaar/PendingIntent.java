package me.cyperion.ntms.Menu.Bazaar;

import me.cyperion.ntms.Bazaar.MarketItem;

public interface PendingIntent {
    void getArgs(MarketItem... args);
    void run();
}
