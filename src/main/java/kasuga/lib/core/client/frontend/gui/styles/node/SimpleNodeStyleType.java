package kasuga.lib.core.client.frontend.gui.styles.node;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;

import java.util.function.Function;

public class SimpleNodeStyleType<T extends Style<?, StyleTarget>> implements StyleType<T, StyleTarget> {

    private final Function<String, T> creator;
    private final T defaultValue;

    SimpleNodeStyleType(Function<String, T> creator, String defaultValue) {
        this.creator = creator;
        this.defaultValue = creator.apply(defaultValue);
    }

    public static <T extends Style<?, StyleTarget>> SimpleNodeStyleType<T> of(Function<String, T> creator, String defaultValue) {
        return new SimpleNodeStyleType<>(creator, defaultValue);
    }

    @Override
    public T getDefault() {
        return defaultValue;
    }

    @Override
    public T create(String string) {
        return creator.apply(string);
    }
}
