package me.cyperion.ntms.Menu;

import me.cyperion.ntms.Class.ClassType;
import me.cyperion.ntms.Class.Explosion;
import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Player.PlayerData;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 職業選擇介面 目前2個職業而已<br>
 * 關聯：/class指令註冊
 */
public class ClassSelectMenu extends Menu {
    ItemStack terminator,explosion,bard;

    public ClassSelectMenu(PlayerMenuUtility utility, NewTMSv8 plugin) {
        super(utility, plugin);
    }

    @Override
    public String getMenuName() {
        return colors("&5選擇職業");
    }

    @Override
    public int getSolts() {
        return 9*3;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        //TODO 點擊職業圖案要選擇
        ItemStack item = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if(!(item.hasItemMeta() && item.getItemMeta().hasCustomModelData())){
            return;
        }
        if(item.getItemMeta().getCustomModelData() == 1008) { //TERMINATOR
            plugin.getPlayerData(player).setClassType(ClassType.TERMINATOR);
            setupTerminator();
            setupExplosion();
            inventory.setItem(11,terminator);
            inventory.setItem(15,explosion);
        }else if(item.getItemMeta().getCustomModelData() == 1009) { //EXPLOSION
            plugin.getPlayerData(player).setClassType(ClassType.EXPLOSION);
            setupTerminator();
            setupExplosion();
            inventory.setItem(11,terminator);
            inventory.setItem(15,explosion);

        }else if(item.getItemMeta().getCustomModelData() == 1009) { //EXPLOSION
            plugin.getPlayerData(player).setClassType(ClassType.EXPLOSION);


        }

    }//口口口口口口口口口

    private void updateMenu(){
        setupTerminator();
        setupExplosion();
        setupBard();
        inventory.setItem(11,terminator);
        inventory.setItem(13,bard);
        inventory.setItem(15,explosion);

    }
    @Override
    public void setMenuItems() {
        //背景
        ItemStack background = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        setupTerminator();
        setupExplosion();

        for(int i = 0; i < 27; i++) {
            inventory.setItem(i, background);
        }
        inventory.setItem(11,terminator);
        inventory.setItem(15,explosion);

    }

    private void setupTerminator(){
        PlayerData data = plugin.getPlayerData(playerMenuUtility.getOwner());
        terminator = new ItemStack(Material.BOW);
        ItemMeta terminatorMeta = terminator.getItemMeta();
        terminatorMeta.setDisplayName(plugin.getTerminator().getName());
        ArrayList<String> terminatorLore = new ArrayList<>();
        terminatorLore.add(colors(""));
        terminatorLore.add(colors("&6&l職業技能&r&f：&6&lSalvation"));
        terminatorLore.add(colors("&f玩家只要拿著&b弓箭&c左鍵&f，即可消耗"));
        terminatorLore.add(colors("&33&f點&b魔力&f並向前方射出&53&f隻箭矢。"));
        terminatorLore.add(colors(""));
        terminatorLore.add(colors("&f每次施放&6&lSalvation&r&f時會獲得"));
        terminatorLore.add(colors("&f一層&dBEAM&f效果，疊滿&33&f層時，會"));
        terminatorLore.add(colors("&f重置層數且此次箭矢傷害&c+15%&f、&2中毒3秒&f"));
        terminatorLore.add(colors(""));
        if(data.getClassType().equals(ClassType.TERMINATOR)){
            terminatorLore.add(colors("&a您目前選擇這個職業"));
            terminatorMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        }else{
            terminatorLore.add(colors("&e點擊選擇職業"));
        }
        terminatorMeta.setLore(terminatorLore);
        terminatorMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        terminatorMeta.setCustomModelData(1008);
        terminator.setItemMeta(terminatorMeta);
    }

    private void setupExplosion(){
        PlayerData data = plugin.getPlayerData(playerMenuUtility.getOwner());
        explosion = new ItemStack(Material.STICK);
        ItemMeta explosionMeta = explosion.getItemMeta();
        explosionMeta.setDisplayName(plugin.getExplosion().getName());
        ArrayList<String> explosionLore = new ArrayList<>();
        explosionLore.add(colors(""));
        explosionLore.add(colors("&6&l職業技能&r&f：&6&lExplosion"));
        explosionLore.add(colors("&f玩家要拿著&c紅魔法杖&f並且蹲下右鍵即可施放"));
        explosionLore.add(colors("&f技能，開始為期&a12&f秒的&d&l詠唱&r&f效果"));
        explosionLore.add(colors("&f結束後對前方一定範圍內的敵人造成&c"+ Explosion.DAMAGE +"&f點"));
        explosionLore.add(colors("&f傷害，並消耗&3"+Explosion.COST_MANA+"&f點&b魔力&f，此技能允許透支魔力"));
        explosionLore.add(colors(""));
        explosionLore.add(colors("&d&l詠唱&r&f："));
        explosionLore.add(colors("&f期間上方進度條會開始充能，並獲得&8緩速效果"));
        explosionLore.add(colors("&f必須全程蹲著才能完整施放，"));
        explosionLore.add(colors(""));
        if(data.getClassType().equals(ClassType.EXPLOSION)){
            explosionLore.add(colors("&a您目前選擇這個職業"));
            explosionMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        }else{
            explosionLore.add(colors("&e點擊選擇職業"));
        }
        explosionMeta.setLore(explosionLore);
        explosionMeta.setCustomModelData(1009);
        explosionMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        explosion.setItemMeta(explosionMeta);
    }

    private void setupBard(){
        PlayerData data = plugin.getPlayerData(playerMenuUtility.getOwner());
        bard = new ItemStack(Material.STICK);
        ItemMeta bardMeta = bard.getItemMeta();
        bardMeta.setDisplayName(plugin.getBard().getName());
        ArrayList<String> bardLore = new ArrayList<>();
        bardLore.add(colors(""));
        bardLore.add(colors("&6&l職業技能&r&f：&3&lBard"));
        bardLore.add(colors("&f玩家只要拿著&d各類山羊角&f吹響即可施放"));
        bardLore.add(colors("&f技能，為周圍的所有玩家提供為期&a15&f秒的&c回復&f、"));
        bardLore.add(colors("&e吸收&f、&5力量&f效果，並且擊退周圍&38格的敵人，"));
        bardLore.add(colors("&f每個山羊角都有特殊的效果："));
        bardLore.add(colors(""));
        bardLore.add(colors(""));
        bardLore.add(colors("&c&l此職業尚未完成"));
        /*
        if(data.getClassType().equals(ClassType.EXPLOSION)){
            bardLore.add(colors("&a您目前選擇這個職業"));
            bardMeta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        }else{
            bardLore.add(colors("&e點擊選擇職業"));
        }
        */
        bardMeta.setLore(bardLore);
        bardMeta.setCustomModelData(1010);
        bardMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        explosion.setItemMeta(bardMeta);
    }
}
