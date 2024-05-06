package kasuga.lib.core.client.gui.style.layout;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.layout.yoga.YogaNode;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleFunctionalHelper;
import kasuga.lib.core.client.gui.style.StyleType;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Filter;

public abstract class EnumStyle<T> extends Style<T> {
    T value;

    public EnumStyle(T value) {
        this.value = value;
    }

    @Override
    public String getValueString() {
        return value.toString();
    }

    @Override
    public T getValue() {
        return value;
    }

    public static class EnumStyleType<T> implements StyleType<Style<T>> {

        private final Function<String, T> type;
        private final BiFunction<T, Map<StyleType<?>, Style<?>>, Boolean> validator;
        private final Style<T> defaultStyle;
        private final StyleFunctionalHelper.StyleAccessor<T> accessor;

        public EnumStyleType(Function<String, T> type, BiFunction<T, Map<StyleType<?>, Style<?>>, Boolean> validator, StyleFunctionalHelper.StyleAccessor<T> accessor, T defaultValue) {
            this.type = type;
            this.validator = validator;
            this.accessor = accessor;
            this.defaultStyle = create(defaultValue.toString());
        }

        public static <T> EnumStyleType<T> of(Function<String,T> type, BiFunction<T, Map<StyleType<?>, Style<?>>, Boolean> validator,StyleFunctionalHelper.StyleAccessor<T> accessor, T defaultValue){
            return new EnumStyleType<>(type, validator,accessor,defaultValue);
        }

        @Override
        public Style<T> getDefault() {
            return defaultStyle;
        }

        @Override
        public Style<T> create(String string) {
            StyleType<?> that = this;
            return new EnumStyle<T>(type.apply(string)) {
                @Override
                public boolean isValid(Map<StyleType<?>, Style<?>> origin) {
                    return validator.apply(value,origin);
                }

                @Override
                public StyleType<?> getType() {
                    return that;
                }

                @Override
                public void apply(Node node) {
                    if(accessor instanceof StyleFunctionalHelper.StyleNodeAccessor<T> nodeAccessor){
                        nodeAccessor.apply(node,value);
                    }
                }

                public void apply(YogaNode yogaNode){
                    if(accessor instanceof StyleFunctionalHelper.StyleYogaNodeAccessor<T> yogaNodeAccessor){
                        yogaNodeAccessor.apply(yogaNode,value);
                    }
                }
            };
        }
    }
}
