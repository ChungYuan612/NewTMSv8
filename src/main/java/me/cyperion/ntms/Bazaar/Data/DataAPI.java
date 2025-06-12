package me.cyperion.ntms.Bazaar.Data;

import me.cyperion.ntms.NewTMSv8;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DataAPI {
    private SQLiteDatabase sqlDatabase;
    private NewTMSv8 plugin;

    public DataAPI( NewTMSv8 plugin) {
        this.plugin = plugin;
        this.sqlDatabase = new SQLiteDatabase(plugin.getDataFolder(), plugin.getLogger());
        this.sqlDatabase.initialize();
    }

    public void test(){
        // 初始化API

        CommodityMarketAPI market = new CommodityMarketAPI(sqlDatabase);

        // 玩家下買單 example
        CommodityMarketAPI.TradeResult result = market.placeBuyOrder("DIAMOND", 64, 100.0, "player123");

        // 玩家下賣單 example
        CommodityMarketAPI.TradeResult result2 = market.placeSellOrder("DIAMOND", 32, 105.0, "player456");

        // 獲取市場數據
        CommodityMarketAPI.MarketData data = market.getMarketData("DIAMOND");
        System.out.println("最高買價: " + data.getHighestBuyPrice());
        System.out.println("最低賣價: " + data.getLowestSellPrice());

        // 查看訂單簿
        Map<String, List<CommodityMarketAPI.Order>> orderbook = market.getOrderBook("DIAMOND", 10);
    }
}