package me.cyperion.ntms.SQL;

import me.cyperion.ntms.NewTMSv7;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends Database{

    private Connection connection;
    private String sqlName;
    private NewTMSv7 plugin;
    private String tableCreation;

    public SQLite(NewTMSv7 plugin) {
        super(plugin);
        this.plugin = plugin;
        sqlName = plugin.getConfig().getString("SQLite.Filename", "NTMS_PlayerData");
        initTable();
        initialize();
    }

    public void initTable(){
        tableCreation = "CREATE TABLE IF NOT EXISTS Players_Eco (" +
                "`uuid` text NOT NULL," +
                "`money` float NOT NULL," +
                "PRIMARY KEY (`uuid`)" +
                ");";

    }

    //(內部方法)
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), sqlName+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+sqlName+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("me.cyperion.ntms.SQL.SQLite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "沒有安裝SQL的JBDC相關套件喔~");
        }
        return null;
    }

    //載入
    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(tableCreation);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
