package me.cyperion.ntms.ItemStacks.Item;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import static me.cyperion.ntms.ItemStacks.ItemRegister.CMD_TREASURE_CORE;
import static me.cyperion.ntms.Utils.colors;

public class EnderCrystal {
    ItemStack item,frag;
    private NewTMSv8 plugin;
    public EnderCrystal(NewTMSv8 plugin) {
        this.plugin = plugin;
        init();
    }
    private void init(){
        item = new ItemStack(Material.PLAYER_HEAD,1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwnerProfile(getProfile());
        meta.setDisplayName(colors("&e&l終界水晶"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(colors("&f破碎的玉核心集合，"));
        lore.add(colors("&f再使用國際黃金修飾。"));
        lore.add(colors("&f可以用&54&f個&5&l碎玉核心&f、"));
        lore.add(colors("&64&f個&6國際黃金&f和"));
        lore.add(colors("&e1&f個&e純金元素&f合成"));
        lore.add(colors(""));
        lore.add(colors("&5超稀有材料"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.UNBREAKING,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.setCustomModelData(CMD_TREASURE_CORE);
        item.setItemMeta(meta);

    }

    private PlayerProfile getProfile(){
        try{
            UUID uuid = UUID.fromString("aa34f543-d7dd-53a6-9cef-2c563f453548");
            PlayerProfile profile = Bukkit.createPlayerProfile(uuid);
            PlayerTextures textures = profile.getTextures();
            String skinUrl = "http://textures.minecraft.net/texture/aa34f543d9dd53a69cef2c563f453548d29a715483aabb8c360b1d07c0bba166"; // Example
            textures.setSkin(URI.create(skinUrl).toURL());
            profile.setTextures(textures);
            return profile;
        }catch (MalformedURLException e){
            e.printStackTrace();
            return null;
        }
    }

    public ItemStack toItemStack(){
        return item.clone();
    }

}
