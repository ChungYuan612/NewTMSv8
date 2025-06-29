package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.ItemStacks.Armors.EmeraldArmor;
import me.cyperion.ntms.ItemStacks.Armors.LapisArmor;
import me.cyperion.ntms.ItemStacks.Armors.ObsidianChestplate;
import me.cyperion.ntms.ItemStacks.Armors.PieceFullBouns;
import me.cyperion.ntms.ItemStacks.Item.InfiniteWindCharge;
import me.cyperion.ntms.ItemStacks.Item.Materaial.*;
import me.cyperion.ntms.ItemStacks.Item.RedWand;
import me.cyperion.ntms.ItemStacks.Tools.PureGoldenPickaxe;
import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * 把物品註冊上去，物品的主類別 上層只有NewTMSv8 plugin
 */
public class ItemRegister {

    private NewTMSv8 plugin;

    public static final int CMD_ENCHANTED_ENDER_PEARL = 4012; //附魔終界珍珠
    public static final int CMD_ENCHANTED_RED_STONE = 4003; //附魔紅石
    public static final int CMD_ENCHANTED_RED_STONE_BLOCK = 4004; //附魔紅石方塊
    public static final int CMD_ENCHANTED_ROTTEN = 4008; //附魔腐肉
    public static final int CMD_ENCHANTED_SEEDS = 4002; //附魔種子
    public static final int CMD_ENCHANTED_SUGAR = 4001; //附魔蔗糖
    public static final int CMD_REINFINED_LAPIS = 4005; //精煉青金石
    public static final int CMD_WIRED_ROTTEN = 4006; //破咒肉塊
    public static final int CMD_ENCHANTED_OBSIDIAN_PART = 4009; //附魔黑曜石碎片
    public static final int CMD_ENCHANTED_OBSIDIAN = 4010; //附魔黑曜石
    public static final int CMD_GOLDEN_ESSENCE = 4011; //純金元素


    public ItemRegister(NewTMSv8 plugin) {
        this.plugin = plugin;
        allPieceFullBouns.add(new LapisArmor(plugin));
        allPieceFullBouns.add(new EmeraldArmor(plugin));
        register();
    }

    private List<PieceFullBouns> allPieceFullBouns = new ArrayList<>();
    private void registerPieceorFullBouns() {
        for(Player player : plugin.getServer().getOnlinePlayers()) {
            for(PieceFullBouns pieceFullBouns : allPieceFullBouns) {
                pieceFullBouns.checkAllArmor(player,player.getInventory().getArmorContents());
                if(pieceFullBouns.isFullSet(player.getInventory().getArmorContents()))
                    pieceFullBouns.addFullBouns(plugin,player);
                else
                    pieceFullBouns.removeFullBouns(plugin,player);
            }
        }

    }

    public void register() {
        registCraftItem();
        plugin.getServer().getPluginManager().registerEvents(new InfiniteWindCharge(plugin),plugin);
        plugin.getServer().getPluginManager().registerEvents(new WiredRotten(plugin),plugin);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                registerPieceorFullBouns();
            }
        };
        runnable.runTaskTimer(plugin,0L,10L);
    }

    private void registCraftItem() {
        Bukkit.getServer().addRecipe(new EnchantedEnderPearl(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new EnchantedRotten(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new WiredRotten(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new LowerWiredRotten(plugin).toNMSRecipe());

        Bukkit.getServer().addRecipe(new EnchantedRedstone(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new EnchantedRedstoneBlock(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new RedWand(plugin).getRecipe());

        Bukkit.getServer().addRecipe(new EnchantedObsidianPart(plugin).toNMSRecipe());
        Bukkit.getServer().addRecipe(new EnchantedObsidian(plugin).toNMSRecipe());

        Bukkit.getServer().addRecipe(new PureGoldenPickaxe(plugin).getNMSRecipe());
        Bukkit.getServer().addRecipe(new ObsidianChestplate(plugin).toNMSRecipe());


        LapisArmor lapisArmor = new LapisArmor(plugin);
        ShapedRecipe[] lapisRrecipe = lapisArmor.toNMSRecipe();
        for(int i = 0; i<4;i++)
            Bukkit.getServer().addRecipe(lapisRrecipe[i]);

        EmeraldArmor emeraldArmor = new EmeraldArmor(plugin);
        ShapedRecipe[] emeraldRecipe = emeraldArmor.toNMSRecipe();
        for(int i = 0; i<4;i++)
            Bukkit.getServer().addRecipe(emeraldRecipe[i]);


        //plugin.getCraftHandler().getRecipes().add(new EnchantedSugar(plugin).getRecipe());
        //plugin.getCraftHandler().getRecipes().add(new EnchantedSeeds(plugin).getRecipe());
    }
}
