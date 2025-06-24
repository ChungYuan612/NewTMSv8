package me.cyperion.ntms.ItemStacks.Item.Materaial;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;

import static me.cyperion.ntms.ItemStacks.ItemRegister.CMD_REINFINED_LAPIS;
import static me.cyperion.ntms.Utils.colors;

/**
 * 這裡做的東西的合成配方放在裝備那裏，註冊也由裝備的Class去ItemRegister註冊了。
 */
public class ReinfinedLapis extends NTMSMaterial{

    public ReinfinedLapis(NewTMSv8 plugin) {
        super(plugin);
    }

    @Override
    protected ArrayList<String> getLore() {
        return new ArrayList<>( Arrays.asList(
                colors("&f從&d突襲活動&f的超強怪物身上掉落"),
                colors("&f看起來很精美的東西，好像可以合成增加"),
                colors("&b魔力&f相關的裝備。")
        ));
    }

    @Override
    public Material getMaterailType() {
        return Material.LAPIS_LAZULI;
    }

    @Override
    public String getItemName() {
        return colors("&9精製的青金石");
    }

    @Override
    public int getCustomModelData() {
        return CMD_REINFINED_LAPIS;
    }

    @Override
    public MaterialRate getMaterialRate() {
        return MaterialRate.RARE;
    }
}