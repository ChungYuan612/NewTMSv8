package me.cyperion.ntms.ItemStacks;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import static me.cyperion.ntms.Utils.colors;

public class CartographyBlocker implements Listener {
    @EventHandler
    public void onCartographyCopy(InventoryClickEvent event) {
        // 判斷是否是製圖台
        if (event.getInventory().getType() != InventoryType.CARTOGRAPHY) return;

        // 判斷是否點選輸出格（完成品）
        if (event.getSlotType() != InventoryType.SlotType.RESULT) return;

        // 取得結果物品
        ItemStack result = event.getCurrentItem();
        if (result == null || result.getType() != Material.FILLED_MAP) return;

        // 取得左右兩格輸入物品
        ItemStack input1 = event.getInventory().getItem(0); // 左上角
        ItemStack input2 = event.getInventory().getItem(1); // 右上角

        // 判斷是否為「複製地圖」的組合：地圖 + 空白地圖
        if (input1 == null || input2 == null) return;

        boolean isCopyingMap =
                (input1.getType() == Material.FILLED_MAP && input2.getType() == Material.MAP) ||
                        (input2.getType() == Material.FILLED_MAP && input1.getType() == Material.MAP);

        if (isCopyingMap) {
            event.setCancelled(true); // 阻止複製
            event.getWhoClicked().sendMessage(colors( "&6[警告] &c此伺服器不允許複製地圖！"));
        }
    }
}
