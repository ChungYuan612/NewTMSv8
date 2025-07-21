package me.cyperion.ntms.ItemStacks.Item;

import me.cyperion.ntms.ItemStacks.ItemRegister;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 碎玉核心。<br>
 * 關聯：PlayerFishEvent註冊和/admin jade取得
 */
public class JadeCore{
    ItemStack item;
    public JadeCore() {
        init();
    }
    private void init(){
        item = new ItemStack(Material.NETHER_BRICK,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colors("&5&l碎玉核心"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&f破碎的玉核心，用來製作紅玉法杖的材料。"));
        lore.add(colors("&f可以透過&b釣魚&f有&50.85%&f機率掉落此核心"));
        lore.add(colors(""));
        lore.add(colors("&5超稀有材料"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(ItemRegister.CMD_JADE_CORE);
        item.setItemMeta(meta);

    }
    public ItemStack toItemStack(){
        return item.clone();
    }
}
