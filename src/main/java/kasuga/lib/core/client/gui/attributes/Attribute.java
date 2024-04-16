package kasuga.lib.core.client.gui.attributes;


import kasuga.lib.core.client.gui.SimpleWidget;

public abstract class Attribute<T> {
    abstract void apply(SimpleWidget widget);
    abstract boolean canApplyTo(SimpleWidget widget);
    abstract T getValue();
    abstract public String toString();

    AttributeType<T> type;

    Attribute<T> setType(AttributeType<T> tAttributeType){
        this.type = tAttributeType;
        return this;
    }

    AttributeType<T> getType(){
        return type;
    }
}
