package kasuga.lib.core.client.gui.style.rendering;

import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleType;

import java.util.function.Function;

public class SimpleStyleType<T extends Style<?>> implements StyleType<T> {
    private final Function<String, T> factory;
    private final T defaultValue;

    public SimpleStyleType(Function<String, T> factory, T defaultValue) {
        this.factory = factory;
        this.defaultValue = defaultValue;
    }

    public static <T extends Style<?>> SimpleStyleType<T> of(Function<String,T> factory, T defaultValue){
        return new SimpleStyleType<>(factory,defaultValue);
    }
    @Override
    public T getDefault() {
        return defaultValue;
    }

    @Override
    public T create(String string) {
        return factory.apply(string);
    }
}
