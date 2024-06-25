package me.cyperion.ntms;

import me.cyperion.ntms.Command.AdminCommand;
import me.cyperion.ntms.Command.EnderChestCommand;
import me.cyperion.ntms.Command.WarpCommand;
import me.cyperion.ntms.Event.DamageIcon;
import me.cyperion.ntms.Event.PlayerAdvanceDoneHandler;
import me.cyperion.ntms.Event.RaidEvent;
import me.cyperion.ntms.Menu.MenuListener;
import me.cyperion.ntms.Menu.PlayerMenuUtility;
import me.cyperion.ntms.Player.PlayerData;
import me.cyperion.ntms.Player.PlayerJoinServerController;
import me.cyperion.ntms.SideBoard.TWPlayerSideBoard;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import me.cyperion.ntms.SideBoard.TMWorldTimer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * 第8季 臺灣地圖伺服器插件
 */
public final class NewTMSv8 extends JavaPlugin {
    private TMWorldTimer tmWorldTimer;
    private TWPlayerSideBoard twPlayerSideBoard;
    //private Database database;
    public final String MAIN_WORLD_NAME = "world";
    public final String RESOURCE_WORLD_NAME = "resource";

    public HashMap<Player, PlayerData> playerData = new HashMap<>();

    public static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    private Economy econ = null;
    private Permission perms = null;
    private Chat chat = null;

    private NSKeyRepo nsKeyRepo;
    private Mana mana;

    public final boolean enableMana = false;

    @Override
    public void onEnable() {

        //資源界
        getServer().createWorld(new org.bukkit.WorldCreator(RESOURCE_WORLD_NAME));
        System.out.println("NTMS伺服器註冊世界：");
        getServer().getWorlds().forEach(world ->
                System.out.println(world.getName())
        );

        //mySQL
        //this.database = new SQLite(this);
        //this.database.load();
        nsKeyRepo = new NSKeyRepo();
        //Mana system
        if(enableMana){
            mana = new Mana(this);
            mana.runTaskTimer(this,0L,20L);
        }
        //Vault
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        //setupChat();

        //記分板系統
        this.tmWorldTimer = new TMWorldTimer(this);
        this.twPlayerSideBoard = new TWPlayerSideBoard(this);
        this.twPlayerSideBoard.runTaskTimer(this,0L,8L);//8刻跑一次，一秒2.5次

        //傷害顯示
        DamageIcon damageIcon = new DamageIcon(this);
        getServer().getPluginManager().registerEvents(damageIcon,this);

        //突襲
        getServer().getPluginManager().registerEvents(new RaidEvent(this),this);
        //成就
        getServer().getPluginManager().registerEvents(new PlayerAdvanceDoneHandler(this),this);
        //玩家登入
        getServer().getPluginManager().registerEvents(new PlayerJoinServerController(this),this);
        //選單控制(這個類別放在./Menu底下)
        getServer().getPluginManager().registerEvents(new MenuListener(),this);

        /* Command */
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("enderchest").setExecutor(new EnderChestCommand());
        getCommand("admin").setExecutor(new AdminCommand(this));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            System.out.println("can't find Vault!");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        System.out.println(getServer().getServicesManager().getKnownServices());
        if (rsp == null) {
            System.out.println("rsp is null!");
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

    public World getResourceWorld(){
        return getServer().getWorld(RESOURCE_WORLD_NAME);
    }

    public World getTWWorld(){
        return getServer().getWorld(MAIN_WORLD_NAME);
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

    public Mana getMana() {
        return mana;
    }

    //特別實作kody simpson的程式
    public static PlayerMenuUtility getPlayerMenuUtility(Player player){
        PlayerMenuUtility utility;
        if(playerMenuUtilityMap.containsKey(player)){
            utility = playerMenuUtilityMap.get(player);
        }else{
            utility = new PlayerMenuUtility(player);
            playerMenuUtilityMap.put(player,utility);
        }
        return utility;
    }

//    public Database getDatabase() {
//        return this.database;
//    }
}
