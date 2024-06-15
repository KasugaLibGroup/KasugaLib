package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.client.frontend.common.style.StyleFunctionalHelper;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class EnumStyle<P,R> extends Style<P,R> {
    P value;
    R target;

    public EnumStyle(P value, R target) {
        this.value = value;
        this.target = target;
    }

    @Override
    public R getTarget() {
        return target;
    }

    @Override
    public StyleType<?, R> getType() {
        return (StyleType<?, R>) this;
    }

    @Override
    public String getValueString() {
        return value.toString();
    }

    @Override
    public P getValue() {
        return value;
    }

    public static class EnumStyleType<T, R> implements StyleType<Style<T, R>, R> {

        private final Function<String, T> type;
        private final Function<String, R> value;
        private final BiFunction<T, Map<StyleType<?, R>, Style<?, R>>, Boolean> validator;
        private final Style<T,R> defaultStyle;
        private final StyleFunctionalHelper.StyleAccessor<T> accessor;

        public EnumStyleType(Function<String, T> type, Function<String, R> value, BiFunction<T, Map<StyleType<?, R>, Style<?, R>>, Boolean> validator, StyleFunctionalHelper.StyleAccessor<T> accessor, T defaultValue) {
            this.type = type;
            this.value = value;
            this.validator = validator;
            this.accessor = accessor;
            this.defaultStyle = create(defaultValue.toString());
        }

        public static <T extends Style<?, R>, R> EnumStyleType<T, R> of(
                Function<String,T> type, Function<String, R> value, BiFunction<T, Map<StyleType<?, R>, Style<?, R>>, Boolean> validator,StyleFunctionalHelper.StyleAccessor<T> accessor, T defaultValue){
            return new EnumStyleType<>(type, value, validator, accessor, defaultValue);
        }

        @Override
        public Style<T, R> getDefault() {
            return defaultStyle;
        }

        @Override
        public Style<T, R> create(String string) {
            StyleType<?, R> that = this;
            return new EnumStyle<T, R>(type.apply(string), value.apply(string)) {
                @Override
                public boolean isValid(Map<StyleType<?, R>, Style<?, R>> origin) {
                    return validator.apply(value, origin);
                }

                @Override
                public StyleType<?, R> getType() {
                    return that;
                }

                public void apply(GuiDomNode node) {
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