package me.cyperion.ntms;

import me.cyperion.ntms.Player.PlayerData;
import me.cyperion.ntms.SideBoard.TWPlayerSideBoard;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import me.cyperion.ntms.SideBoard.TMWorldTimer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class NewTMSv8 extends JavaPlugin {
    private TMWorldTimer tmWorldTimer;
    private TWPlayerSideBoard twPlayerSideBoard;
    //private Database database;
    public final String MAIN_WORLD_NAME = "world";
    public final String RESOURCE_WORLD_NAME = "resource";

    public HashMap<Player, PlayerData> playerData = new HashMap<>();

    private Economy econ = null;
    private Permission perms = null;
    private Chat chat = null;

    private NSKeyRepo nsKeyRepo;
    private Mana mana;

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
        nsKeyRepo = new NSKeyRepo();
        mana = new Mana(this);
        mana.runTaskTimer(this,0L,20L);

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

    public Economy getEconomy() {
        return econ;
    }

    public Permission getPermissions() {
        return perms;
    }

    public Chat getChat() {
        return chat;
    }

    public TMWorldTimer getTmWorldTimer() {
        return tmWorldTimer;
    }

    public NSKeyRepo getNsKeyRepo() {
        return nsKeyRepo;
    }

    public PlayerData getPlayerData(Player player){
        return playerData.get(player);
    }

//    public Database getDatabase() {
//        return this.database;
//    }
}
