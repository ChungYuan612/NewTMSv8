package me.cyperion.ntms;

import me.cyperion.ntms.SQL.Database;
import me.cyperion.ntms.SQL.SQLite;
import me.cyperion.ntms.SideBoard.TMWorldTimer;
import org.bukkit.plugin.java.JavaPlugin;

public final class NewTMSv7 extends JavaPlugin {
    private TMWorldTimer tmWorldTimer;
    private Database database;
    @Override
    public void onEnable() {
        this.tmWorldTimer = new TMWorldTimer(this);
        this.database = new SQLite(this);
        this.database.load();
    }

    public TMWorldTimer getTmWorldTimer() {
        return tmWorldTimer;
    }


    public Database getDatabase() {
        return this.database;
    }
}
