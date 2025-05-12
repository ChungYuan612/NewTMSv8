package me.cyperion.ntms.ItemStacks.Item;

import me.cyperion.ntms.NSKeyRepo;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static me.cyperion.ntms.Utils.colors;

/**
 * 台灣股票和金價的物品。<br>
 * 所有的股票都放在這裡了
 */
public class Stocks {

    private NewTMSv8 plugin;

    public Stocks(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public ItemStack getItem(StockType type){
        return type.getItem(plugin);
    }


    public enum StockType{
        s3607(false, ChatColor.DARK_AQUA,
                "上華實業","3607",
                Arrays.asList(
                        colors("&f這家公司以各式土木工程為主"),
                        colors("&f分紅的部分主要以&3建材&f為主。"),
                        colors(""),
                        colors("&6&l金融證券")
                )
        ),
        s3391(false, ChatColor.DARK_GREEN,
                "凱薩物流","3391",
                Arrays.asList(
                        colors("&f這家公司以物流為主業"),
                        colors("&f很樂意以現金回饋給各位股東"),
                        colors("&f分紅以&3現金&f為主，偶以&3稀有礦物&f代替"),
                        colors(""),
                        colors("&6&l金融證券")
                )
        ),
        s3230(false, ChatColor.YELLOW,
                "誠品國際","3230",
                Arrays.asList(
                        colors("&f這家公司以國際貿易為主"),
                        colors("&f除了會分紅現金，有小機率"),
                        colors("&f取得&3稀有特殊材料&f、&3各種材料"),
                        colors(""),
                        colors("&6&l金融證券")
                )
        ),
        xaud(true, ChatColor.GOLD,
                "國際黃金","XAUD",
                Arrays.asList(
                        colors("&f國際金融的硬通貨"),
                        colors("&f可以用於合成特殊物品"),
                        colors(""),
                        colors("&6&l金融證券")
                )
        );
        boolean isGold;
        ChatColor color;
        String name;
        String number;
        List<String> lores;
        StockType(boolean isGold, ChatColor color, String name, String number,List<String> lores) {
            this.isGold = isGold;
            this.color = color;
            this.name = name;
            this.number = number;
            this.lores = lores;
        }
        public ItemStack getItem(NewTMSv8 plugin){
            ItemStack item;
            if(this.isGold)
                item = new ItemStack(Material.GOLD_INGOT);
            else
                item = new ItemStack(Material.PAPER);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(colors(this.color+"&l"+this.name));
            meta.addEnchant(Enchantment.UNBREAKING,1,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> array = new ArrayList<>();
            this.lores.forEach(s -> array.add(colors(s)));
            meta.setLore(array);
            meta.setCustomModelData(3000+this.ordinal());
            meta.getPersistentDataContainer()
                            .set(
                                    plugin.getNsKeyRepo().getKey(
                                            NSKeyRepo.KEY_ITEM_STOCK_ID
                                    ),
                                    PersistentDataType.STRING,
                                    this.number
                            );
            meta.getPersistentDataContainer()
                    .set(
                            plugin.getNsKeyRepo().getKey(
                                    NSKeyRepo.KEY_ITEM_STOCK_LAST_REWARD
                            ),
                            PersistentDataType.INTEGER,
                            0
                    );
            item.setItemMeta(meta);

            return item.clone();
        }
    }

}
