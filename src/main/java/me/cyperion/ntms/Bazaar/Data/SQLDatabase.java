package me.cyperion.ntms.Bazaar.Data;

import java.sql.Connection;

/**
 * SQLDatabase介面（需要根據你的實際實現調整）
 */
public interface SQLDatabase {
    enum Type {
        SQLITE, MYSQL
    }

    Connection getConnection();
    Type getType();
}
