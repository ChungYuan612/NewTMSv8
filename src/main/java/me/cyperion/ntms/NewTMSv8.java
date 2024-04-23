package me.cyperion.ntms;

import me.cyperion.ntms.SideBoard.TWPlayerSideBoard;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import me.cyperion.ntms.SideBoard.TMWorldTimer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class NewTMSv8 extends JavaPlugin {
    private TMWorldTimer tmWorldTimer;
    private TWPlayerSideBoard twPlayerSideBoard;
    //private Database database;
    public final String MAIN_WORLD_NAME = "world";

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    @Override
    public void onEnable() {
        //Vault
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();

        //my
        //this.database = new SQLite(this);
        //this.database.load();
        //記分板系統
        this.tmWorldTimer = new TMWorldTimer(this);
        this.twPlayerSideBoard = new TWPlayerSideBoard(this);
        this.twPlayerSideBoard.runTaskTimer(this,0L,8L);//8刻跑一次，一秒2.5次

    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public TWPlayerSideBoard getTwPlayerSideBoard() {
        return twPlayerSideBoard;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    public TMWorldTimer getTmWorldTimer() {
        return tmWorldTimer;
    }


//    public Database getDatabase() {
//        return this.database;
//    }
}
