package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.ItemStacks.Item.*;
import me.cyperion.ntms.ItemStacks.Item.Materaial.*;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NTMSItemFactory {

    private final NewTMSv8 plugin;

    public NTMSItemFactory(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public ItemStack getNTMSItem(String name){
        ItemStack item ;
        if(name.equals(NTMSItems.RED_WAND.name())){
            item = new RedWand(plugin).toItemStack();
        }else if(name.equals(NTMSItems.JADE_CORE.name())){
            item = new JadeCore().toItemStack();
        }else if(name.equals(NTMSItems.EMERALD_COINS.name())) {
            item = new Emerald_Coins().toItemStack();
        }else if(name.equals(NTMSItems.INFINITE_WIND_CHARGE.name())) {
            item = new InfiniteWindCharge(plugin).toItemStack();
        }else if(name.equals(NTMSItems.ENCHANTED_SEEDS.name())) {
            item = new EnchantedSeeds(plugin).toItemStack();
        }else if(name.equals(NTMSItems.ENCHANTED_SUGAR.name())) {
            item = new EnchantedSugar(plugin).toItemStack();
        }else if(name.equals(NTMSItems.ENCHANTED_ROTTEN.name())) {
            item = new EnchantedRotten(plugin).toItemStack();
        }else if(name.equals(NTMSItems.ENCHANTED_ENDER_PEARL.name())) {
            item = new EnchantedEnderPearl(plugin).toItemStack();
        }else if(name.equals(NTMSItems.WIRED_ROTTEN.name())) {
            item = new WiredRotten(plugin).toItemStack();
        }else if(name.equals(NTMSItems.LOWER_WIRED_ROTTEN.name())) {
            item = new LowerWiredRotten(plugin).toItemStack();
        }else if(name.equals(NTMSItems.ENCHANTED_RED_STONE.name())) {
            item = new EnchantedRedstone(plugin).toItemStack();
        }else if(name.equals(NTMSItems.ENCHANTED_RED_STONE_BLOCK.name())) {
            item = new EnchantedRedstoneBlock(plugin).toItemStack();
        }else if(name.equals(NTMSItems.STOCK_3607.name())) {
            item = new Stocks(plugin).getItem(Stocks.StockType.s3607);
        }else if(name.equals(NTMSItems.STOCK_3230.name())) {
            item = new Stocks(plugin).getItem(Stocks.StockType.s3230);
        }else if(name.equals(NTMSItems.STOCK_3391.name())) {
            item = new Stocks(plugin).getItem(Stocks.StockType.s3391);
        }else if(name.equals(NTMSItems.STOCK_XAUD.name())) {
            item = new Stocks(plugin).getItem(Stocks.StockType.xaud);
        }else if(name.equals(NTMSItems.REINFINED_LAPIS.name())) {
            item = new ReinfinedLapis(plugin).toItemStack();
        }else{
            item = new ItemStack(Material.BARRIER);
        }
        return item;
    }
}