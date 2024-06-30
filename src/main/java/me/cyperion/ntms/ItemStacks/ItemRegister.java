package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.ItemStacks.Item.Item_InfiniteWindCharge;
import me.cyperion.ntms.NewTMSv8;

public class ItemRegister {

    private NewTMSv8 plugin;

    public ItemRegister(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getServer().getPluginManager().registerEvents(new Item_InfiniteWindCharge(),plugin);

    }
}
