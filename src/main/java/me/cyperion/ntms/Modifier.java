package me.cyperion.ntms;


/**
 * <h3>數值更改器</h3>
 * 使用方法：創建這個類別，NamespaceKey參考NSKeyRepo的字串，直接套進來就可以了
 *
 */
public class Modifier {

    private String nameSpaceKey;
    private ModifierType type;
    private String ID;
    private double value;

    public Modifier(String ID, String nameSpaceKey, ModifierType type, double value) {
        this.ID = ID;
        this.nameSpaceKey = nameSpaceKey;
        this.type = type;
        this.value = value;
    }

    public String getID() {
        return ID;
    }

    public String getNameSpaceKey() {
        return nameSpaceKey;
    }

    public void setNameSpaceKey(String nameSpaceKey) {
        this.nameSpaceKey = nameSpaceKey;
    }

    public ModifierType getType() {
        return type;
    }

    public void setType(ModifierType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
