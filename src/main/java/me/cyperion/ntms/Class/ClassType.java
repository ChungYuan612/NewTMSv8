package me.cyperion.ntms.Class;

public enum ClassType {
    TERMINATOR("&dTerminator"),
    EXPLOSION("&cエクスプロージョン"),
    HYPERION("&6太陽神"),
    TANK("&f俱收並蓄"),
    NONE(""),;
    String name;
    ClassType(String name){
        this.name = name;
    }
}
