package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * 控制合成的地方，管理所有CraftRecipe配方的合成<br>
 * 關聯：NewTMSv8註冊<br>
 * 因為另外註冊合成配方需要經過這裡，所以ItemRegister也會註冊材料(目前)上來
 */
public class CraftHandler implements Listener {
    private NewTMSv8 plugin;

    private final ArrayList<CraftRecipe> recipes = new ArrayList<>();
    public CraftHandler(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCrafting(PrepareItemCraftEvent event){
        if(event.isRepair()) return;
        var matrix = new CraftRecipe(event.getInventory().getMatrix());

        recipes.forEach(new Consumer<CraftRecipe>() {
            @Override
            public void accept(CraftRecipe recipe) {

                if(matrix.isSimilar(recipe)){
                    //System.out.println("craft:true");
                    event.getInventory().setResult(recipe.getResult());
                    //合成完把所有材料都刪除
                    ItemStack[] newMatrix = new ItemStack[9];
                    for(int i = 0;i<9;i++){
                        newMatrix[i] = new ItemStack(Material.AIR);
                    }
                    //event.getInventory().setMatrix(newMatrix);
//                    for(int i =0;i<9;i++)
//                        System.out.println(i+"="+matrix.getItemInSolt(i).toString() + " : "+recipe.getItemInSolt(i).toString());
                }
            }
        });
    }
    public void addRecipe(CraftRecipe recipe){
        recipes.add(recipe);
    }

    public ArrayList<CraftRecipe> getRecipes() {
        return recipes;
    }
}
