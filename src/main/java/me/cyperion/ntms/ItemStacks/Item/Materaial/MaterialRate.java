package me.cyperion.ntms.ItemStacks.Item.Materaial;

public enum MaterialRate{
    NORMAL("&2","普通"),
    RARE("&3","稀有"),
    EPIC("&5","罕見"),
    LEGENDARY("&6","傳說");
    String color;
    String rateName;
    MaterialRate(String color,String rateName) {
        this.color = color;
        this.rateName = rateName;
    }

    public String toLoreNoColor(){
        return color+rateName;
    }
}

