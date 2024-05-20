package me.cyperion.ntms.Class;

import me.cyperion.ntms.NewTMSv8;

//自訂職業
public abstract class Class {
    protected NewTMSv8 plugin;

    public Class(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    public abstract ClassType getClassType();
}
