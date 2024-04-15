package me.cyperion.ntms.SQL;

import me.cyperion.ntms.NewTMSv7;
import me.cyperion.ntms.SQL.SQLError.SQLError;
import me.cyperion.ntms.SQL.SQLError.SQLErrors;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

public abstract class Database {
    NewTMSv7 plugin;
    Connection connection;
    public String table = "Players_Eco";
    public int tokens = 0;
    public Database(NewTMSv7 instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE player = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }

    //
    public Float getMoney(Player player) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String uuid = player.getUniqueId().toString();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '"+uuid+"';");

            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getString("uuid").equalsIgnoreCase(uuid)){
                    return rs.getFloat("money");
                }
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, SQLErrors.sqlConnectionExecute(), ex);

        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, SQLErrors.sqlConnectionClose(), ex);
            }
        }
        return -1f;
    }

    //
    public void setMoney(Player player, Float money) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO " + table + " (uuid,money) VALUES(?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.

            ps.setString(1, player.getUniqueId().toString());
            ps.setFloat(2, money);
            ps.executeUpdate();

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, SQLErrors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, SQLErrors.sqlConnectionClose(), ex);
            }
        }
    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            SQLError.close(plugin, ex);
        }
    }
}
