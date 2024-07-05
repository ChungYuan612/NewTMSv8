package me.cyperion.ntms.ItemStacks;

import me.cyperion.ntms.NewTMSv8;
import org.bukkit.inventory.ItemStack;

public class CraftRecipe {
    private ItemStack[] matrix;
    private ItemStack result;

    public CraftRecipe(ItemStack[] matrix) {
        if(matrix.length != 9){
            this.matrix = new ItemStack[9];

            System.arraycopy(matrix,0,this.matrix,0,matrix.length);
        }
        this.matrix = matrix;
    }

    public CraftRecipe(ItemStack[] matrix, ItemStack result){
        this.result = result;
        if(matrix.length != 9){
            this.matrix = new ItemStack[9];
            System.arraycopy(matrix,0,this.matrix,0,matrix.length);
        }
        this.matrix = matrix;
    }

    public CraftRecipe(ItemStack[][] matrix){
        this.matrix = new ItemStack[9];
        for(int i = 0;i<3;i++){
            System.arraycopy(matrix[i], 0, this.matrix, i * 3, 3);
        }
    }

    public CraftRecipe(ItemStack[][] matrix, ItemStack result){
        this.result = result;
        this.matrix = new ItemStack[9];
        for(int i = 0;i<3;i++){
            System.arraycopy(matrix[i], 0, this.matrix, i * 3, 3);
        }
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean isSimilar(CraftRecipe recipe){
        var equals = recipe.getMatrix();
        boolean pass = true;
        for(int i = 0;i<9;i++){
            if(!ItemEquals(matrix[i],equals[i])){
                pass = false;
                break;
            }
        }
        return pass;
    }

    public ItemStack getItemInSolt(int ver,int hor){
        return getItemInSolt(ver*3+hor);
    }

    public ItemStack getItemInSolt(int solt){
        return matrix[solt];
    }

    public ItemStack[] getMatrix() {
        return matrix;
    }

    public static boolean ItemEquals(ItemStack a, ItemStack b){

        if(a == null  && b == null){
            //a and b is null
            return true;
        }else if(a == null || b == null){
            //a or b is null
            return false;
        }
        //用Material比較
        if(a.getType() != b.getType()){
            return false;
        }
        if(a.getAmount() != b.getAmount()){
            //System.out.println("ab數量不同");
            return false;
        }
        //用CustomModelData比較
        if(a.hasItemMeta() && b.hasItemMeta() && !a.isSimilar(b)){

            var am = a.getItemMeta().getCustomModelData();
            var bm = b.getItemMeta().getCustomModelData();
            //System.out.println("aCMD:"+am+": "+"bCMD:"+bm);
            return am == bm;
        }
        //System.out.println("a.isSimilar b");
        //a or b hasn't itemMeta , a isn't Similar b
        return a.isSimilar(b);
    }
}
