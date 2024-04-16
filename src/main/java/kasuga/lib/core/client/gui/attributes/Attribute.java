package kasuga.lib.core.client.gui.attributes;


import kasuga.lib.core.client.gui.SimpleWidget;

public abstract class Attribute<T> {
    public abstract void apply(SimpleWidget widget);
    public abstract boolean canApplyTo(SimpleWidget widget);
    public abstract T getValue();
    public abstract String toString();

    AttributeType<T> type;

    public Attribute<T> setType(AttributeType<T> tAttributeType){
        this.type = tAttributeType;
        return this;
    }

    public AttributeType<T> getType(){
        return type;
    }
}
