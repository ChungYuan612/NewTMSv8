package me.cyperion.ntms.Menu;

import me.cyperion.ntms.ItemStacks.Item.InfiniteWindCharge;
import me.cyperion.ntms.ItemStacks.Item.Materaial.GoldenEssence;
import me.cyperion.ntms.ItemStacks.Item.MysteryTurtleEgg;
import me.cyperion.ntms.ItemStacks.Item.Stocks;
import me.cyperion.ntms.ItemStacks.NTMSItems;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

enum ShopItem{

    NTMS_XAUD(NTMSItems.STOCK_XAUD, 1, 100,100),
    INFINITE_WIND_CHARGE(NTMSItems.INFINITE_WIND_CHARGE, 1, 100000,-1),
    GOLDEN_ESSENCE(NTMSItems.GOLDEN_ESSENCE, 1, 51200,5120),
    OAK_LOG(Material.OAK_LOG, 1, 18,-1),
    SPRUCE_LOG(Material.SPRUCE_LOG, 1, 18,-1),
    BIRCH_LOG(Material.BIRCH_LOG, 1, 18,-1),
    CHERRY_LOG(Material.CHERRY_LOG, 1, 18,-1),
    STONE(Material.STONE, 1, 8,-1),
    DIORITE(Material.DIORITE, 1, 8,-1),
    COBBLED_DEEPSLATE(Material.COBBLED_DEEPSLATE, 1, 9,-1),
    SAND(Material.SAND, 1, 8,-1),
    GRAVEL(Material.GRAVEL, 1, 8,-1),
    CLAY_BALL(Material.CLAY_BALL, 1, 7,-1),
    SLIME_BALL(Material.SLIME_BALL, 1, 25,-1),
    GLOW_INK_SAC(Material.GLOW_INK_SAC, 1, 32,-1),
    ARROW(Material.ARROW, 1, 5,-1),
    RED_STONE(Material.REDSTONE, 1, 18,-1),
    QUARTZ(Material.QUARTZ, 1, 9,-1),
    AMETHYST_SHARD(Material.AMETHYST_SHARD, 1, 11,-1),
    OCHRE_FROGLIGHT(Material.OCHRE_FROGLIGHT, 1, 100,-1),
    PEARLESCENT_FROGLIGHT(Material.PEARLESCENT_FROGLIGHT, 1, 100,-1),
    VERDANT_FROGLIGHT(Material.VERDANT_FROGLIGHT, 1, 100,-1),
    END_CRYSTAL(Material.END_CRYSTAL, 1, 2500,-1),
    LIGHT(Material.LIGHT, 1, 100,-1),
    BARRIER(Material.BARRIER, 1, 50,-1),
    PLAYER_HEAD(Material.PLAYER_HEAD, 1, 20,-1),
    SADDLE(Material.SADDLE, 1, -1,100),
    TOTEM_OF_UNDYING(Material.TOTEM_OF_UNDYING, 1, 900,80),
    WHITE_BANNER(Material.WHITE_BANNER, 1, -1,10),
    EMERALD(Material.EMERALD, 1, -1,3),
    NETHERITE_INGOT(Material.NETHERITE_INGOT, 1, -1,800),
    MYSTERY_TURTLE_EGG(NTMSItems.MYSTERY_TURTLE_EGG, 1, -1,250000),


    ;
    String itemName;
    boolean isCustomItem;
    final int amount;
    double buyPrice;
    double sellPrice;//-1 代表不給賣
    ItemStack item;
    NTMSItems ntmsItems;

    ShopItem(Material material, int amount, double buyPrice, double sellPrice) {
        this.item = new ItemStack(material,amount);
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    ShopItem(NTMSItems ntmsItems, int amount, double buyPrice, double sellPrice) {
        this.amount = amount;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.ntmsItems = ntmsItems;
        this.isCustomItem = true;
    }

    public String getItemName(NewTMSv8 plugin){
        checkItem(plugin);
        if(!item.hasItemMeta()){
            ItemStack a = new ItemStack(item.getType());

            return a.getItemMeta().getItemName();
        }
        return this.item.getItemMeta().getItemName();
    }

    public void checkItem(NewTMSv8 plugin) {
        if(isCustomItem){
            if(ntmsItems.equals(NTMSItems.STOCK_XAUD)) {
                this.item = new Stocks(plugin).getItem(Stocks.StockType.xaud);
            }else if (ntmsItems.equals(NTMSItems.INFINITE_WIND_CHARGE)) {
                this.item = new InfiniteWindCharge(plugin).toItemStack();
            }else if (ntmsItems.equals(NTMSItems.GOLDEN_ESSENCE)) {
                this.item = new GoldenEssence(plugin).toItemStack();
            }else if (ntmsItems.equals(NTMSItems.MYSTERY_TURTLE_EGG)) {
                this.item = new MysteryTurtleEgg().toItemStack();
            }
        }
    }

    public ItemStack toDisplay(NewTMSv8 plugin) {
        checkItem(plugin);

        ItemStack item = this.item.clone();
        if(item.getType() == Material.PLAYER_HEAD){
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            meta.setDisplayName(colors("&d自己的頭顱"));
            item.setItemMeta(meta);
        }
        ItemMeta meta =  item.getItemMeta();
        ArrayList<String> lore;
        if (meta.hasLore()) lore = (ArrayList<String>) meta.getLore();
        else lore = new ArrayList<>();
        lore.add(colors(""));
        if(buyPrice != -1)
            lore.add(colors("&f買入價格：&6"+ String.format("%,.1f", buyPrice)));
        if(sellPrice != -1)
            lore.add(colors("&f賣出價格：&6"+ String.format("%,.1f", sellPrice)));
        lore.add(colors(""));
        if(buyPrice != -1){
            lore.add(colors("&e左鍵買入"));
            lore.add(colors("&eShift左鍵買入一組"));
        }
        if(sellPrice != -1){
            lore.add(colors("&e右鍵賣出"));
            lore.add(colors("&eShift右鍵賣出一組"));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item.clone();
    }
}