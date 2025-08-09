package me.cyperion.ntms;

import com.loohp.holomobhealth.api.HoloMobHealthAPI;
import me.cyperion.ntms.Bazaar.Data.CommodityMarketAPI;
import me.cyperion.ntms.Bazaar.Data.SQLiteDatabase;
import me.cyperion.ntms.Class.Bard;
import me.cyperion.ntms.Class.Class;
import me.cyperion.ntms.Class.Explosion;
import me.cyperion.ntms.Class.Terminator;
import me.cyperion.ntms.Command.*;
import me.cyperion.ntms.Damage.ArmorStandPacket;
import me.cyperion.ntms.Damage.DamageIcon;
import me.cyperion.ntms.Damage.DamageIndicator;
import me.cyperion.ntms.Event.*;
import me.cyperion.ntms.ItemStacks.CartographyBlocker;
import me.cyperion.ntms.ItemStacks.CraftHandler;
import me.cyperion.ntms.ItemStacks.ItemRegister;
import me.cyperion.ntms.ItemStacks.NTMSItemFactory;
import me.cyperion.ntms.Menu.BaseMenu.MenuListener;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.Monster.MonsterRegister;
import me.cyperion.ntms.Player.PlayerData;
import me.cyperion.ntms.Player.PlayerJoinServerController;
import me.cyperion.ntms.Player.PlayerQuitServer;
import me.cyperion.ntms.SQL.TradeDatabaseManager;
import me.cyperion.ntms.SideBoard.NTMSEvents;
import me.cyperion.ntms.SideBoard.TWPlayerSideBoard;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import me.cyperion.ntms.SideBoard.TMWorldTimer;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static me.cyperion.ntms.Utils.colors;

/**
 * 第8季 臺灣地圖伺服器插件
 */
public final class NewTMSv8 extends JavaPlugin {

    private TradeDatabaseManager dbManager;

    public boolean UNDER_MAINTENANCE = false; //是否正在維修
    private TMWorldTimer tmWorldTimer;
    private TWPlayerSideBoard twPlayerSideBoard;
    private CraftHandler craftHandler;
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
    private ModifierMain modifierMain;

    private SQLiteDatabase sqlDatabase;
    private CommodityMarketAPI commodityMarketAPI;
    private NTMSItemFactory factory;

    private Class explosion,terminator,bard;

    public final boolean enableMana = true;

    private NTMSEvents ntmsEvents;

    private DamageIndicator damageIndicator;
    private ArmorStandPacket armorStandPacket;

    private EnderDragonBossSystem bossSystem;


    @Override
    public void onEnable() {
        //this.getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        getServer().setMotd(colors(
                "              "+"&6&lNTMS &e臺灣地圖伺服器 &a"+getConfig().getString("Version") + "\n" +
                "         "+"&9爆擊&f與&d終界BOSS更新&f...還有很多更新！ &b歡迎加入!")
        );


        //資源界
        getServer().createWorld(new org.bukkit.WorldCreator(RESOURCE_WORLD_NAME));
        System.out.println("NTMS伺服器註冊世界：");
        getServer().getWorlds().forEach(world ->
                System.out.println(world.getName())
        );
        //mySQL
        //this.database = new SQLite(this);
        //this.database.load();
        sqlDatabase = new SQLiteDatabase(this.getDataFolder(), this.getLogger());
        sqlDatabase.initialize();
        commodityMarketAPI = new CommodityMarketAPI(sqlDatabase);
        factory = new NTMSItemFactory(this);

        nsKeyRepo = new NSKeyRepo();
        //Mana system
        if(enableMana){
            mana = new Mana(this);
            mana.runTaskTimer(this,0L,20L);
        }
        modifierMain = new ModifierMain(this);
        //Vault
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        //setupChat();

        //HoloMobHealthAPI


        //記分板系統
        this.tmWorldTimer = new TMWorldTimer(this);
        this.twPlayerSideBoard = new TWPlayerSideBoard(this);
        getServer().getPluginManager().registerEvents(twPlayerSideBoard,this);
        this.twPlayerSideBoard.runTaskTimer(this,0L,8L);//8刻跑一次，一秒2.5次
        this.ntmsEvents = new NTMSEvents();

        damageIndicator = new DamageIndicator(this);
        getServer().getPluginManager().registerEvents(damageIndicator,this);
        armorStandPacket = new ArmorStandPacket();
        ArmorStandPacket.update();

        bossSystem = new EnderDragonBossSystem(this);
        bossSystem.initialize();

        //傷害顯示
        //DamageIcon damageIcon = new DamageIcon(this);
        //getServer().getPluginManager().registerEvents(damageIcon,this);



        //突襲
        getServer().getPluginManager().registerEvents(new RaidEvent(this),this);
        //成就
        getServer().getPluginManager().registerEvents(new PlayerAdvanceDoneHandler(this),this);
        //玩家登入
        getServer().getPluginManager().registerEvents(new PlayerJoinServerController(this),this);
        //玩家登出
        getServer().getPluginManager().registerEvents(new PlayerQuitServer(this),this);
        //玩家聊天
        getServer().getPluginManager().registerEvents(new PlayerChatHandler(this),this);
        //玩家釣魚
        getServer().getPluginManager().registerEvents(new PlayerFishingEvent(this),this);
        //選單控制(這個類別放在./Menu底下)
        getServer().getPluginManager().registerEvents(new MenuListener(),this);

        /* Command */
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("warp").setTabCompleter(new WarpCommand(this));
        getCommand("enderchest").setExecutor(new EnderChestCommand());
        getCommand("admin").setExecutor(new AdminCommand(this));
        getCommand("admin").setTabCompleter(new AdminCommand(this));
        getCommand("menu").setExecutor(new MenuCommand(this));
        getCommand("ntms").setExecutor(new NTMSCommand());
        getCommand("signin").setExecutor(new SigninCommand(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("class").setExecutor(new ClassCommand(this));
        getCommand("sign").setExecutor(new SignCommand(this));
        getCommand("dragonboss").setExecutor(new DragonBossCommand(this));


        //TPA 3個指令
        TpaCommand tpaCommand = new TpaCommand();
        getCommand("tpa").setExecutor(tpaCommand);
        getCommand("tpaccept").setExecutor(tpaCommand);
        getCommand("tpadeny").setExecutor(tpaCommand);


        //Craft
        //this.craftHandler = new CraftHandler(this);
        //getServer().getPluginManager().registerEvents(craftHandler,this);
        getServer().getPluginManager().registerEvents(new CartographyBlocker(),this);

        //Monster 目前關閉 只開啟突襲的部分
        this.getServer().getPluginManager().registerEvents(new MonsterRegister(this),this);

        ItemRegister register = new ItemRegister(this);

        explosion = new Explosion(this);
        terminator = new Terminator(this);
        bard = new Bard(this);
        getServer().getPluginManager().registerEvents(new Terminator(this),this);
        getServer().getPluginManager().registerEvents(new Explosion(this),this);
        getServer().getPluginManager().registerEvents(new Bard(this),this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        
        DamageIcon.damageIcons
                .forEach((entity, integer) ->
                        entity.remove()
                );

        modifierMain.clearModifier();
        modifierMain.run();
        
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

    public NTMSEvents getNtmsEvents() {
        return ntmsEvents;
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

    public CraftHandler getCraftHandler() {
        return craftHandler;
    }

    public SQLiteDatabase getSqlDatabase() {
        return sqlDatabase;
    }

    public NTMSItemFactory getFactory() {
        return factory;
    }

    public CommodityMarketAPI getCommodityMarketAPI() {
        return commodityMarketAPI;
    }

    public EnderDragonBossSystem getBossSystem() {
        return bossSystem;
    }

    //自定義Config

    public Configuration getConfigFile(String name){
        File file = new File(this.getDataFolder(), name + ".yml");

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return YamlConfiguration.loadConfiguration(file);
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

    public ModifierMain getModifierMain() {
        return modifierMain;
    }

    public Class getExplosion() {
        return explosion;
    }

    public Class getTerminator() {
        return terminator;
    }

    public Class getBard() {
        return bard;
    }

    //    public Database getDatabase() {
//        return this.database;
//    }
}
