package kasuga.lib.core.client.gui.style;

import kasuga.lib.core.client.gui.components.Node;

import java.util.HashMap;
import java.util.Map;

public abstract class Style<P> {

    public abstract boolean isValid(Map<StyleType<?>, Style<?>> origin);

    public abstract StyleType<?> getType();

    public abstract void apply(Node node);

    public abstract String getValueString();

    public abstract P getValue();

    public String toString(){
        return StyleRegistry.getStyleName(this.getType()) + ":" + this.getValueString();
    }
}
