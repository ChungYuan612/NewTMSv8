package me.cyperion.ntms.Menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

enum ShopItem{

    ;
    String itemName;
    ArrayList<String> lores;
    Material material;
    int amount;
    double price;
    ItemStack item;
    ItemStack displayItem;
    int solt;

    ShopItem(String itemName, ArrayList<String> lores, Material material, int amount, double price, int solt) {
        this.itemName = itemName;
        this.lores = lores;
        this.material = material;
        this.amount = amount;
        this.price = price;
        this.solt = solt;
        createDisplay();
    }

    ShopItem(String itemName, int amount, double price, ItemStack item) {
        this.itemName = itemName;
        this.amount = amount;
        this.price = price;
        this.item = item;
    }

    private void createDisplay() {

    }
}