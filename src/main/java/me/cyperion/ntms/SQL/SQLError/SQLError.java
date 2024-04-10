package me.cyperion.ntms.SQL.SQLError;

import me.cyperion.ntms.NewTMSv7;

import java.util.logging.Level;

public class SQLError {
    public static void execute(NewTMSv7 plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "無法執行SQL執行式: ", ex);
    }
    public static void close(NewTMSv7 plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "關閉SQL連接時遇到錯誤: ", ex);
    }
}
