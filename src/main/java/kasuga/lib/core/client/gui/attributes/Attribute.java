package kasuga.lib.core.client.gui.attributes;


import kasuga.lib.core.client.gui.SimpleWidget;

public interface Attribute<T> {
    void apply(SimpleWidget widget);
    boolean canApplyTo(SimpleWidget widget);
    T getValue();
    String toString();
}
