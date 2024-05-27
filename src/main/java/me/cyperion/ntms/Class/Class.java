package me.cyperion.ntms.Class;

import me.cyperion.ntms.NewTMSv8;

//自訂職業
public abstract class Class extends ClassUpgrade {

    public Class(NewTMSv8 plugin) {
        super(plugin);
    }

    public abstract ClassType getClassType();

    public abstract String getName();
}
