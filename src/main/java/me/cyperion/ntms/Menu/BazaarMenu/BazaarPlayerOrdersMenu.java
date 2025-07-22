package me.cyperion.ntms.Menu.BazaarMenu;

import me.cyperion.ntms.Bazaar.Data.CommodityMarketAPI;
import me.cyperion.ntms.ItemStacks.NTMSItemFactory;
import me.cyperion.ntms.Menu.BaseMenu.Menu;
import me.cyperion.ntms.Menu.BaseMenu.PlayerMenuUtility;
import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static me.cyperion.ntms.Utils.colors;

// ============================================================================
// BazaarPlayerOrdersMenu.java - 玩家訂單管理介面
// ============================================================================
public class BazaarPlayerOrdersMenu extends Menu {

    private final CommodityMarketAPI tradingAPI;
    private final String playerId;
    private ItemStack background;
    private ItemStack backButton;
    private ItemStack refreshButton;
    private List<CommodityMarketAPI.Order> playerOrders;
    private int currentPage = 0;
    private final int ordersPerPage = 28; // 7x4 的訂單顯示區域

    public BazaarPlayerOrdersMenu(PlayerMenuUtility utility, NewTMSv8 plugin,
                                  CommodityMarketAPI tradingAPI, String playerId) {
        super(utility, plugin);
        this.tradingAPI = tradingAPI;
        this.playerId = playerId;
        init();
    }

    @Override
    public String getMenuName() {
        return colors("&3我的訂單管理");
    }

    @Override
    public int getSolts() {
        return 6 * 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if (!event.isLeftClick()) return;
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 45: // 返回按鈕
                new BazaarMenu(this.playerMenuUtility, plugin).open();
                break;

            case 53: // 刷新按鈕
                loadPlayerOrders();
                setMenuItems();
                player.sendMessage(colors("&a訂單列表已刷新！"));
                break;

            case 46: // 上一頁
                if (currentPage > 0) {
                    currentPage--;
                    setMenuItems();
                }
                break;

            case 52: // 下一頁
                int maxPages = (int) Math.ceil((double) playerOrders.size() / ordersPerPage);
                if (currentPage < maxPages - 1) {
                    currentPage++;
                    setMenuItems();
                }
                break;

            default:
                // 檢查是否點擊了訂單項目
                if (slot >= 10 && slot <= 43 && slot % 9 != 0 && slot % 9 != 8) {
                    handleOrderClick(player, slot);
                }
                break;
        }
    }

    @Override
    public void setMenuItems() {
        // 清空庫存
        inventory.clear();

        // 填充背景
        for (int i = 0; i < getSolts(); i++) {
            inventory.setItem(i, background);
        }

        // 設置固定按鈕
        inventory.setItem(45, backButton);
        inventory.setItem(53, refreshButton);

        // 設置頁面控制按鈕
        if (currentPage > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevPage.getItemMeta();
            prevMeta.setDisplayName(colors("&e上一頁"));
            List<String> prevLore = new ArrayList<>();
            prevLore.add(colors("&7當前頁面：" + (currentPage + 1)));
            prevMeta.setLore(prevLore);
            prevPage.setItemMeta(prevMeta);
            inventory.setItem(46, prevPage);
        }

        int maxPages = (int) Math.ceil((double) playerOrders.size() / ordersPerPage);
        if (currentPage < maxPages - 1) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextPage.getItemMeta();
            nextMeta.setDisplayName(colors("&e下一頁"));
            List<String> nextLore = new ArrayList<>();
            nextLore.add(colors("&7當前頁面：" + (currentPage + 1) + "/" + maxPages));
            nextMeta.setLore(nextLore);
            nextPage.setItemMeta(nextMeta);
            inventory.setItem(52, nextPage);
        }

        // 顯示頁面信息
        ItemStack pageInfo = new ItemStack(Material.PAPER);
        ItemMeta pageMeta = pageInfo.getItemMeta();
        pageMeta.setDisplayName(colors("&6訂單統計"));
        List<String> pageLore = new ArrayList<>();
        pageLore.add(colors("&7總訂單數：&b" + playerOrders.size()));
        pageLore.add(colors("&7當前頁面：&e" + (currentPage + 1) + "/" + Math.max(1, maxPages)));

        // 統計買單和賣單數量
        int buyOrderCount = 0;
        int sellOrderCount = 0;
        double totalBuyValue = 0;
        double totalSellValue = 0;
        int claimableOrders = 0;

        for (CommodityMarketAPI.Order order : playerOrders) {
            if (order.getOrderType() == CommodityMarketAPI.OrderType.BUY) {
                buyOrderCount++;
                totalBuyValue += order.getUnitPrice() * order.getRemaining();
            } else {
                sellOrderCount++;
                totalSellValue += order.getUnitPrice() * order.getRemaining();
            }

            // 統計可領取的訂單
            if (order.getClaimed() < order.getFilled()) {
                claimableOrders++;
            }
        }

        pageLore.add(colors(""));
        pageLore.add(colors("&a買單：" + buyOrderCount + " 筆"));
        pageLore.add(colors("&c賣單：" + sellOrderCount + " 筆"));
        pageLore.add(colors("&e可領取：" + claimableOrders + " 筆"));
        pageLore.add(colors("&7買單總值：&6" + String.format("%,.2f", totalBuyValue) + " 元"));
        pageLore.add(colors("&7賣單總值：&6" + String.format("%,.2f", totalSellValue) + " 元"));
        pageMeta.setLore(pageLore);
        pageInfo.setItemMeta(pageMeta);
        inventory.setItem(49, pageInfo);

        // 顯示當前頁面的訂單
        int startIndex = currentPage * ordersPerPage;
        int endIndex = Math.min(startIndex + ordersPerPage, playerOrders.size());

        int displaySlot = 10; // 從第二排第二個位置開始
        for (int i = startIndex; i < endIndex; i++) {
            CommodityMarketAPI.Order order = playerOrders.get(i);
            ItemStack orderDisplay = createOrderDisplay(order, i);
            inventory.setItem(displaySlot, orderDisplay);

            // 計算下一個顯示位置（跳過邊框）
            displaySlot++;
            if (displaySlot % 9 == 8) { // 如果到了右邊邊界
                displaySlot += 2; // 跳到下一行的第二個位置
            }
            if (displaySlot >= 44) break; // 避免超出顯示區域
        }
    }

    private void handleOrderClick(Player player, int slot) {
        // 計算點擊的是哪個訂單
        int displayIndex = getOrderIndexFromSlot(slot);
        int actualIndex = currentPage * ordersPerPage + displayIndex;

        if (actualIndex >= 0 && actualIndex < playerOrders.size()) {
            CommodityMarketAPI.Order order = playerOrders.get(actualIndex);

            try {
                // 檢查是否有可領取的成交量
                if (order.getClaimed() < order.getFilled()) {
                    // 有可領取的物品或金錢
                    handleClaimOrder(player, order);
                } else if (order.getRemaining() > 0) {
                    // 沒有可領取的，但訂單還未完全成交，可以取消
                    handleCancelOrder(player, order);
                } else {
                    // 訂單已完全成交且已領取完畢
                    player.sendMessage(colors("&7此訂單已完全成交並領取完畢"));
                }
            } catch (Exception e) {
                player.sendMessage(colors("&c處理訂單時發生錯誤！"));
                e.printStackTrace();
            }
        }
    }

    /**
     * 處理領取訂單獎勞
     */
    private void handleClaimOrder(Player player, CommodityMarketAPI.Order order) {
        try {
            int claimableAmount = order.getFilled() - order.getClaimed();

            if (claimableAmount <= 0) {
                player.sendMessage(colors("&c沒有可領取的物品或金錢！"));
                return;
            }

            if (order.getOrderType() == CommodityMarketAPI.OrderType.BUY) {
                // 買單 - 領取購買的物品
                String productId = order.getProductId();

                // 確保productId格式正確（去掉前綴 "ntms:"）
                String itemId = productId.startsWith("NTMS_") ? productId.substring(5) : productId;

                ItemStack item = plugin.getFactory().getNTMSItem(itemId);
                if (item != null) {
                    Utils.giveItem(player, item, claimableAmount);
                    player.sendMessage(colors("&a成功領取 &6" + claimableAmount + " &a個 &b" + itemId + " &a！"));
                } else {
                    player.sendMessage(colors("&c無法找到物品：" + itemId));
                    return;
                }
            } else {
                // 賣單 - 領取販售所得的金錢
                double price = order.getUnitPrice();
                double totalEarnings = price * claimableAmount;

                plugin.getEconomy().depositPlayer(player, totalEarnings);
                player.sendMessage(colors("&a成功領取 &6" + String.format("%,.2f", totalEarnings) + " &a元！"));
            }

            // 更新已領取數量
            tradingAPI.claimOrder(order.getId(), order.getFilled());

            // 刷新訂單列表
            loadPlayerOrders();
            setMenuItems();

        } catch (Exception e) {
            player.sendMessage(colors("&c領取訂單時發生錯誤！"));
            e.printStackTrace();
        }
    }

    /**
     * 處理取消訂單
     */
    private void handleCancelOrder(Player player, CommodityMarketAPI.Order order) {
        try {
            player.sendMessage(colors("&e正在取消訂單 #" + order.getId() + "..."));

            boolean success = tradingAPI.cancelOrder(order.getId(), playerId);
            if (success) {
                player.sendMessage(colors("&a成功取消訂單 #" + order.getId()));

                // 退還相應資源
                if (order.getOrderType() == CommodityMarketAPI.OrderType.BUY) {
                    // 買單退還金錢
                    double refund = order.getUnitPrice() * order.getRemaining();
                    plugin.getEconomy().depositPlayer(player, refund);
                    player.sendMessage(colors("&a已返還 &6" + String.format("%.2f", refund) + " &a元"));
                } else {
                    // 賣單退還物品
                    String productId = order.getProductId();
                    String itemId = productId.startsWith("NTMS_") ? productId.substring(5) : productId;

                    ItemStack item = plugin.getFactory().getNTMSItem(itemId);
                    if (item != null) {
                        Utils.giveItem(player, item, order.getRemaining());
                        player.sendMessage(colors("&a已返還 &6" + order.getRemaining() + " &a個 &b" + itemId));
                    }
                }

                // 刷新訂單列表
                loadPlayerOrders();
                setMenuItems();
            } else {
                player.sendMessage(colors("&c取消訂單失敗！訂單可能已經被執行或不存在。"));
            }
        } catch (Exception e) {
            player.sendMessage(colors("&c取消訂單時發生錯誤！"));
            e.printStackTrace();
        }
    }

    private int getOrderIndexFromSlot(int slot) {
        // 將庫存槽位轉換為訂單索引
        int row = slot / 9;
        int col = slot % 9;

        if (row < 1 || row > 4 || col < 1 || col > 7) {
            return -1; // 無效位置
        }

        return (row - 1) * 7 + (col - 1);
    }

    private ItemStack createOrderDisplay(CommodityMarketAPI.Order order, int index) {
        // 根據訂單狀態決定顯示材質
        Material displayMaterial;
        String statusColor;

        if (order.getClaimed() < order.getFilled()) {
            // 有可領取的內容
            displayMaterial = Material.CHEST;
            statusColor = "&e";
        } else if (order.getRemaining() > 0) {
            // 訂單還在進行中
            displayMaterial = order.getOrderType() == CommodityMarketAPI.OrderType.BUY ? Material.EMERALD : Material.REDSTONE;
            statusColor = order.getOrderType() == CommodityMarketAPI.OrderType.BUY ? "&a" : "&c";
        } else {
            // 訂單已完成
            displayMaterial = Material.PAPER;
            statusColor = "&7";
        }

        ItemStack display = new ItemStack(displayMaterial);
        ItemMeta meta = display.getItemMeta();

        String orderTypeText = order.getOrderType() == CommodityMarketAPI.OrderType.BUY ? "買單" : "賣單";
        meta.setDisplayName(colors(statusColor + orderTypeText + " #" + order.getId()));

        List<String> lore = new ArrayList<>();
        lore.add(colors(""));

        // 基本訂單信息
        String productId = order.getProductId();
        String itemId = productId.startsWith("NTMS_") ? productId.substring(5) : productId;
        lore.add(colors("&7商品：&b" + itemId));
        lore.add(colors("&7單價：&6" + String.format("%.2f", order.getUnitPrice()) + " 元"));
        lore.add(colors("&7總量：&b" + order.getAmount()));
        lore.add(colors("&7已成交：&e" + order.getFilled()));
        lore.add(colors("&7剩餘：&7" + order.getRemaining()));
        lore.add(colors(""));

        // 顯示訂單狀態和操作說明
        if (order.getClaimed() < order.getFilled()) {
            // 有可領取的內容
            int claimableAmount = order.getFilled() - order.getClaimed();
            lore.add(colors("&2&l✓ 可領取內容："));

            if (order.getOrderType() == CommodityMarketAPI.OrderType.BUY) {
                lore.add(colors("&a  → &6" + claimableAmount + " &a個 &b" + itemId));
            } else {
                double earnings = claimableAmount * order.getUnitPrice();
                lore.add(colors("&a  → &6" + String.format("%.2f", earnings) + " &a元"));
            }

            lore.add(colors(""));
            lore.add(colors("&e&l點擊領取獎勵"));

        } else if (order.getRemaining() > 0) {
            // 訂單進行中，可以取消
            lore.add(colors("&7狀態：&a進行中"));
            lore.add(colors(""));
            lore.add(colors("&c左鍵點擊取消訂單"));
            lore.add(colors("&c&l注意：取消後無法恢復！"));

        } else {
            // 訂單已完成
            lore.add(colors("&7狀態：&2已完成"));
        }

        // 顯示下單時間
        if (order.getCreatedAt().toEpochSecond(ZoneOffset.UTC) > 0) {
            long timeAgo = System.currentTimeMillis() - (order.getCreatedAt().toEpochSecond(ZoneOffset.UTC) * 1000);
            String timeStr = formatTimeAgo(timeAgo);
            lore.add(colors(""));
            lore.add(colors("&7下單時間：&7" + timeStr + "前"));
        }

        meta.setLore(lore);
        display.setItemMeta(meta);
        return display;
    }

    private String getOrderStatus(CommodityMarketAPI.Order order) {
        if (order.getClaimed() < order.getFilled()) {
            return "&e可領取";
        } else if (order.getRemaining() > 0) {
            return "&a進行中";
        } else {
            return "&7已完成";
        }
    }

    private String formatTimeAgo(long millisAgo) {
        long seconds = millisAgo / 1000;
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            return (seconds / 60) + "分鐘";
        } else if (seconds < 86400) {
            return (seconds / 3600) + "小時";
        } else {
            return (seconds / 86400) + "天";
        }
    }

    private void loadPlayerOrders() {
        try {
            playerOrders = tradingAPI.getPlayerOrders(playerId);
            if (playerOrders == null) {
                playerOrders = new ArrayList<>();
            }

            // 按優先級排序：可領取的在前，然後按時間排序
            playerOrders.sort((o1, o2) -> {
                // 首先按是否可領取排序
                boolean o1Claimable = o1.getClaimed() < o1.getFilled();
                boolean o2Claimable = o2.getClaimed() < o2.getFilled();

                if (o1Claimable && !o2Claimable) return -1;
                if (!o1Claimable && o2Claimable) return 1;

                // 如果領取狀態相同，按時間排序（最新的在前）
                return Long.compare(
                        o2.getCreatedAt().toEpochSecond(ZoneOffset.UTC),
                        o1.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
            });
        } catch (Exception e) {
            playerOrders = new ArrayList<>();
            e.printStackTrace();
        }
    }

    private void init() {
        // 背景
        background = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta backgroundMeta = background.getItemMeta();
        backgroundMeta.setDisplayName(" ");
        background.setItemMeta(backgroundMeta);

        // 返回按鈕
        backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(colors("&e返回市場"));
        List<String> backLore = new ArrayList<>();
        backLore.add(colors("&7點擊返回市場主頁"));
        backMeta.setLore(backLore);
        backButton.setItemMeta(backMeta);

        // 刷新按鈕
        refreshButton = new ItemStack(Material.CLOCK);
        ItemMeta refreshMeta = refreshButton.getItemMeta();
        refreshMeta.setDisplayName(colors("&b刷新訂單"));
        List<String> refreshLore = new ArrayList<>();
        refreshLore.add(colors("&7點擊重新載入訂單列表"));
        refreshMeta.setLore(refreshLore);
        refreshButton.setItemMeta(refreshMeta);

        // 載入玩家訂單
        loadPlayerOrders();
    }
}