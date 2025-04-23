package me.cyperion.ntms.ItemStacks.Item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

import static me.cyperion.ntms.Utils.colors;

/**
 * 綠報時貨幣。<br>
 * 關聯：/admin emerald取得
 */
public class Emerald_Coins{
    ItemStack item;

    private void init(){
        item = new ItemStack(Material.EMERALD,1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colors("&a&l綠寶石貨幣"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&7七週年晚會活動貨幣，臺灣伺服器第一"));
        lore.add(colors("&7個貨幣，感覺這個貨幣可以換到什麼好"));
        lore.add(colors("&7康的東西?又或者有其他用途?"));
        lore.add(colors(""));
        lore.add(colors("&6紀念幣"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(4007);
        item.setItemMeta(meta);

    }
    public ItemStack toItemStack(){
        if(item == null)
            init();
        return item.clone();
    }
}
