package me.cyperion.ntms.Menu;

import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.Bukkit;
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

    private ItemStack close;
    private ItemStack playerHead;

    private ItemStack goBed;
    private ItemStack goResource;
    private ItemStack goTW;
    private ItemStack shop;
    private ItemStack market;
    private ItemStack changeMana;
    private ItemStack enderChest;



    public TWMainMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility,plugin);
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
            }else if ( item.equals(goBed)) {
                player.performCommand("warp bed");
            }else if ( item.equals(goResource)) {
                player.performCommand("warp rs");
            }else if ( item.equals(goTW)) {
                player.performCommand("warp tw");
            }else if ( item.equals(enderChest)) {
                player.performCommand("enderchest");
            }
        }

        if(item.equals(shop) || item.equals(market)){
            player.sendMessage(colors("&c&l正在維修中..."));
        }

        if(item.hasItemMeta() && item.getItemMeta().getCustomModelData() == 1006){
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

        List<String> clickLore = new ArrayList<>();
        clickLore.add("");
        clickLore.add(colors("&e左鍵點擊"));

        //背景
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        //關閉按鈕
        close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(colors("&c關閉"));
        closeMeta.setLore(clickLore);
        close.setItemMeta(closeMeta);

        //玩家頭顱
        playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)playerHead.getItemMeta();
        meta.setDisplayName(colors(
                player.getDisplayName()+" "+ChatColor.WHITE+"的個人數值"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.RED+SpecialChar("❤")+"血量: "+(int)(player.getHealth())+colors("&f/&c")+(int)(player.getHealthScale()));
        lore.add(ChatColor.AQUA+SpecialChar("✯")+"魔力上限: "+(int)(playerData.getMaxMana()));
        lore.add(ChatColor.WHITE+SpecialChar("✦")+"速度: "+(int)((player.getWalkSpeed()+1)*100)+"%");
        lore.add(ChatColor.GOLD+SpecialChar("☣")+"成就點數: "+playerData.getAdvancePoint());
        lore.add(ChatColor.BLUE+SpecialChar("☠")+"突襲計算: "+playerData.getRaidPoint());
        lore.add(ChatColor.GREEN+SpecialChar("☘")+"幸運等級: "+ChatColor.RED+"(COMING SOON)");
        lore.add("");
        lore.add(player.getPing()<50?ChatColor.GREEN+"⫽Ping:"+player.getPing():ChatColor.YELLOW+"⫽Ping:"+player.getPing());
        lore.add("");
        lore.add(ChatColor.GRAY+"歡迎來到臺灣地圖伺服器!");
        meta.setLore(lore);
        meta.setOwningPlayer(player);
        playerHead.setItemMeta(meta);

        //床重生點
        goBed = new ItemStack(Material.RED_BED);
        ItemMeta goBedMeta = goBed.getItemMeta();
        goBedMeta.setDisplayName(colors("&d傳送至床重生點  &8/warp bed"));
        goBedMeta.setCustomModelData(1001);//未來做資源包可用
        goBedMeta.setLore(clickLore);
        goBed.setItemMeta(goBedMeta);

        //資源界傳送
        goResource = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta goResourceMeta = goResource.getItemMeta();
        goResourceMeta.setDisplayName(colors("&d傳送至資源界 &8/warp rs"));
        goResourceMeta.setCustomModelData(1002);//未來做資源包可用
        goResourceMeta.setLore(clickLore);
        goResource.setItemMeta(goResourceMeta);

        //傳送至TW
        goTW = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta goTWMeta = goTW.getItemMeta();
        goTWMeta.setDisplayName(colors("&d傳送至臺灣地圖 &8/warp tw"));
        goTWMeta.setCustomModelData(1003);//未來做資源包可用
        goTWMeta.setLore(clickLore);
        goTW.setItemMeta(goTWMeta);

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
        shopLore.add(colors("&c&l目前尚未開放!"));
        shopMeta.setLore(shopLore);
        shopMeta.setCustomModelData(1004);//未來做資源包可用
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
        marketLore.add(colors("&c&l目前尚未開放!"));
        marketMeta.setLore(marketLore);
        marketMeta.setCustomModelData(1005);//未來做資源包可用
        market.setItemMeta(marketMeta);

        //切換顯示魔力
        changeMana = toggleManaShow(playerData.getShowManaOnActionbar());

        //終界箱
        enderChest = new ItemStack(Material.ENDER_CHEST);
        ItemMeta enderChestMeta = enderChest.getItemMeta();
        enderChestMeta.setDisplayName(colors("&5終界箱 &8/enderchest"));
        enderChestMeta.setCustomModelData(1007);//未來做資源包可用
        enderChestMeta.setLore(clickLore);
        enderChest.setItemMeta(enderChestMeta);


        //設定上去Menu

        for(int i = 0; i < 27; i++){
            inventory.setItem(i,background);
        }

        inventory.setItem(4,playerHead);
        inventory.setItem(10,goBed);
        inventory.setItem(11,goResource);
        inventory.setItem(12,goTW);
        inventory.setItem(13,shop);
        inventory.setItem(14,market);
        inventory.setItem(15,changeMana);
        inventory.setItem(16,enderChest);
        inventory.setItem(22,close);

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
        changeManaMeta.setCustomModelData(1006);
        item.setItemMeta(changeManaMeta);

        return item;
    }
}
