package kasuga.lib.core.client.gui.attributes;

import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import kasuga.lib.core.client.gui.SimpleWidget;
import kasuga.lib.core.client.gui.layout.ElementLocator;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class LocatorAttributeOperator {
    public static <T> NonNullBiConsumer<SimpleWidget,T> asLocator(BiConsumer<ElementLocator,T> f){
        return (w,t)->{
            f.accept(w.getElementLocator(),t);
            w.triggerLocate();
        };
    }
}
