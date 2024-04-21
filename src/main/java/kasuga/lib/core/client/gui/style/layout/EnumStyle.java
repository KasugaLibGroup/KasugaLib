package kasuga.lib.core.client.gui.style.layout;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleType;

import java.util.Map;

public class EnumStyle<T> extends Style<T> {
    @Override
    public boolean isValid(Map<StyleType<?>, Style<?>> origin) {
        return true;
    }

    @Override
    public StyleType<?> getType() {
        return null;
    }

    @Override
    public void apply(Node node) {

    }

    @Override
    public String getValueString() {
        return null;
    }

    @Override
    public T getValue() {
        return null;
    }
}
