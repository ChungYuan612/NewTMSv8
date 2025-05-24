package me.cyperion.ntms.Menu;

import me.cyperion.ntms.Command.SigninCommand;
import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.SpecialChar;
import static me.cyperion.ntms.Utils.colors;
//TODO
/**
 * <h2>台灣地圖的主要選單</h2>
 * 可以透過/menu來打開<br>
 * 照著KodySimpson EP.55的教學
 */
public class TWMainMenu extends Menu {

    private ItemStack background;
    private ItemStack close;
    private ItemStack playerHead;

    private ItemStack signin;
    private ItemStack warp;
    private ItemStack classes;
    private ItemStack shop;
    private ItemStack market;
    private ItemStack changeMana;
    private ItemStack enderChest;

    private final int SIGNIN_CMD = 1001;
    private final int WARP_CMD = 1002;
    private final int CLASS_CMD = 1002;
    private final int SHOP_CMD = 1004;
    private final int MARKET_CMD = 1005;
    private final int MANA_CMD = 1006;
    private final int ENDER_CMD = 1007;



    public TWMainMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility,plugin);
        loadStaticItems();
    }

    @Override
    public String getMenuName() {
        return colors("&8主選單");
    }

    @Override
    public int getSolts() {
        return 9*3;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {

        Player player = playerMenuUtility.getOwner();
        PlayerData playerData = plugin.getPlayerData(player);
        ItemStack item = event.getCurrentItem();

        //warp和關閉選單和終界箱
        if(event.isLeftClick()){
            if(item.equals(close)){
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }else if(item.isSimilar(warp)){
                player.closeInventory();
                player.performCommand("menu warp");
            } else if ( item.isSimilar(enderChest)) {
                player.performCommand("enderchest");
            }else if(item.isSimilar(classes)){
                player.performCommand("class");
            }else if(item.isSimilar(shop)){
                player.closeInventory();
                player.performCommand("menu shop");
            }else if(item.isSimilar(market)){
                player.performCommand("bazaar");
                //player.performCommand("menu market");
                //player.sendMessage(colors("&c&l正在維修中..."));
            }
        }


        if(item.isSimilar(signin)){
            if(signin.getItemMeta().getCustomModelData() == SIGNIN_CMD){
                player.performCommand("signin");
                player.closeInventory();
            }else if (signin.getItemMeta().getCustomModelData() == 1008){
                player.sendMessage(colors("&c你已經簽到過了"));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);

            }

        }

        if(item.hasItemMeta() && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == MANA_CMD){
            if(playerData.getShowManaOnActionbar()){
                player.sendMessage(colors("&c已關閉魔力顯示"));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }else{
                player.sendMessage(colors("&a已開啟魔力顯示"));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
            playerData.setShowManaOnActionbar(!playerData.getShowManaOnActionbar());

            inventory.setItem(15,toggleManaShow(playerData.getShowManaOnActionbar()));
        }
    }

    @Override
    public void setMenuItems() {
        Player player = playerMenuUtility.getOwner();
        PlayerData playerData = plugin.getPlayerData(player);

        //玩家頭顱
        playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)playerHead.getItemMeta();
        meta.setDisplayName(colors(
                "&b"+player.getName()+" "+ChatColor.WHITE+"的個人數值"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.RED+SpecialChar("❤")+"血量: "+(int)(player.getHealth())+colors("&f/&c")+(int)(player.getHealthScale()));
        lore.add(ChatColor.AQUA+SpecialChar("✯")+"魔力上限: "+(int)(playerData.getMaxMana()));
        lore.add(ChatColor.WHITE+SpecialChar("✦")+"速度: "+(int)((player.getWalkSpeed()+1)*100)+"%");
        lore.add(ChatColor.GOLD+SpecialChar("☣")+"成就點數: "+playerData.getAdvancePoint());
        lore.add(ChatColor.BLUE+SpecialChar("☠")+"突襲計算: "+playerData.getRaidPoint());
        lore.add(ChatColor.DARK_GREEN+SpecialChar("⦾")+"累積簽到: "+playerData.getSignInCount());
        lore.add(ChatColor.GREEN+SpecialChar("☘")+"幸運值: "+String.format("%.1f",playerData.getLuck()));
        lore.add("");
        lore.add(player.getPing()<50?ChatColor.GREEN+"⫽Ping:"+player.getPing():ChatColor.YELLOW+"⫽Ping:"+player.getPing());
        lore.add("");
        lore.add(ChatColor.GRAY+"歡迎來到臺灣地圖伺服器!");
        meta.setLore(lore);
        meta.setOwningPlayer(player);
        playerHead.setItemMeta(meta);

        //每日簽到按鈕
        signin = getSigninItem(player);

        //切換顯示魔力
        changeMana = toggleManaShow(playerData.getShowManaOnActionbar());

        //設定上去Menu

        for(int i = 0; i < 27; i++){
            inventory.setItem(i,background.clone());
        }

        inventory.setItem(4,playerHead);
        inventory.setItem(10,signin);
        inventory.setItem(11,warp);
        inventory.setItem(12,classes);
        inventory.setItem(13,shop);
        inventory.setItem(14,market);
        inventory.setItem(15,changeMana);
        inventory.setItem(16,enderChest);
        inventory.setItem(22,close);

    }

    private void loadStaticItems(){

        List<String> clickLore = new ArrayList<>();
        clickLore.add("");
        clickLore.add(colors("&e左鍵點擊"));

        //背景
        background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        //關閉按鈕
        close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(colors("&c關閉"));
        closeMeta.setLore(clickLore);
        close.setItemMeta(closeMeta);

        //地圖傳送
        warp = new ItemStack(Material.COMPASS);
        ItemMeta warpMeta = warp.getItemMeta();
        warpMeta.setDisplayName(colors("&b地圖傳送"));
        warpMeta.setCustomModelData(WARP_CMD);//未來做資源包可用
        ArrayList<String> warpLore = new ArrayList<>();
        warpLore.add("");
        warpLore.add(colors("&7這裡可以使用地圖傳送功能"));
        warpLore.add("");
        warpLore.add(colors("&e點擊打開地圖傳送選單"));
        warpMeta.setLore(warpLore);
        warp.setItemMeta(warpMeta);

        //職業選擇
        classes = new ItemStack(Material.BOOK);
        ItemMeta classesMeta = classes.getItemMeta();
        classesMeta.setDisplayName(colors("&3職業選擇"));
        classesMeta.setCustomModelData(CLASS_CMD);//未來做資源包可用
        ArrayList<String> classesLore = new ArrayList<>();
        classesLore.add(colors("&8/class"));
        classesLore.add(colors("&7這裡可以使用職業選擇清單功能"));
        classesLore.add("");
        classesLore.add(colors("&e左鍵打開"));
        classesMeta.setLore(classesLore);
        classes.setItemMeta(classesMeta);

        //商城(系統)
        shop = new ItemStack(Material.EMERALD);
        ItemMeta shopMeta = shop.getItemMeta();
        shopMeta.setDisplayName(colors("&6商城"));
        ArrayList<String> shopLore = new ArrayList<>();
        shopLore.add("");
        shopLore.add(colors("&7以現金交易的商店，應有盡有，在這裡"));
        shopLore.add(colors("&7你可以買到許多種建材、素材等等......"));
        shopLore.add(colors("&7甚至還可以買到稀有物品呢!"));
        shopLore.add(colors(""));
        shopLore.add(colors("&e點擊打開商城"));
        //shopLore.add(colors("&c&l目前尚未開放!"));
        shopMeta.setLore(shopLore);
        shopMeta.setCustomModelData(SHOP_CMD);//未來做資源包可用
        shop.setItemMeta(shopMeta);

        //市場(玩家)
        market = new ItemStack(Material.GOLD_INGOT);
        ItemMeta marketMeta = market.getItemMeta();
        marketMeta.setDisplayName(colors("&b市場"));
        ArrayList<String> marketLore = new ArrayList<>();
        marketLore.add("");
        marketLore.add(colors("&7以現金交易的市場，可以在此與玩家"));
        marketLore.add(colors("&7交易各種東西，包含股票等等的交易"));
        marketLore.add(colors("&7也在此處進行。"));
        marketLore.add(colors(""));
        marketLore.add(colors("&e點擊打開市場"));
        //marketLore.add(colors("&c&l目前尚未開放!"));
        marketMeta.setLore(marketLore);
        marketMeta.setCustomModelData(MARKET_CMD);//未來做資源包可用
        market.setItemMeta(marketMeta);

        //終界箱
        enderChest = new ItemStack(Material.ENDER_CHEST);
        ItemMeta enderChestMeta = enderChest.getItemMeta();
        enderChestMeta.setDisplayName(colors("&5終界箱 &8/enderchest"));
        enderChestMeta.setCustomModelData(ENDER_CMD);//未來做資源包可用
        enderChestMeta.setLore(clickLore);
        enderChest.setItemMeta(enderChestMeta);

    }

    private ItemStack toggleManaShow(boolean showMana){
        ItemStack item;
        if(showMana)
            item = new ItemStack(Material.LIME_DYE);
        else
            item = new ItemStack(Material.GRAY_DYE);

        ItemMeta changeManaMeta = item.getItemMeta();
        changeManaMeta.setDisplayName(colors("&d切換顯示魔力"));
        ArrayList<String> changeManaLore = new ArrayList<>();
        if(showMana){
            changeManaLore.add(colors("&f目前狀態： &a顯示"));
            changeManaLore.add(colors("&e點擊隱藏"));
        }else{
            changeManaLore.add(colors("&f目前狀態： &c隱藏"));
            changeManaLore.add(colors("&e點擊顯示"));
        }
        changeManaMeta.setLore(changeManaLore);
        changeManaMeta.setCustomModelData(MANA_CMD);
        item.setItemMeta(changeManaMeta);

        return item;
    }

    private ItemStack getSigninItem(Player player) {
        boolean isSigned = SigninCommand.isSigned(player);

        ItemStack item;
        if(!isSigned){
            item = new ItemStack(Material.CHEST_MINECART);
        }else{
            item = new ItemStack(Material.MINECART);
        }

        ItemMeta signinMeta = item.getItemMeta();
        signinMeta.setDisplayName(colors("&a每日簽到"));

        ArrayList<String> signinLore = new ArrayList<>();
        signinLore.add("");
        signinLore.add(colors("&3每日簽到&7可以獲得&61500元&7+其他在線"));
        signinLore.add(colors("&7人數&6x100元&7的獎勵，您目前簽到會獲得"));
        signinLore.add(colors("&6大約"+ SigninCommand.countSigninMoney(plugin.getPlayerData(player).getSignInCount())+"元&7，在你簽到的時候其他"));
        signinLore.add(colors("&7在線玩家也會獲得&65~40元&7的獎勵。"));
        signinLore.add(colors(""));
        if(!isSigned){//還沒簽到
            signinLore.add(colors("&e點擊簽到"));
            signinMeta.setCustomModelData(SIGNIN_CMD);//未來做資源包可用
        }else{//已經簽到
            signinLore.add(colors("&c今日已經簽到過了"));
            signinMeta.setCustomModelData(1008);//未來做資源包可用
        }
        signinMeta.setLore(signinLore);
        item.setItemMeta(signinMeta);

        return item;
    }
}
