package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class EnumStyle<E> extends LayoutStyle<E> {

    public final String original;
    public final E value;
    private final BiFunction<E, Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>>, Boolean> validator;

    public EnumStyle(
            E value,
            BiFunction<E, Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>>, Boolean> validator
    ) {
        this.value = value;
        this.original = value.toString();
        this.validator = validator;
    }

    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return validator.apply(value, origin);
    }

    @Override
    public String getValueString() {
        return value.toString();
    }

    @Override
    public E getValue() {
        return value;
    }

    public static class EnumStyleType<E> implements StyleType<EnumStyle<E>, StyleTarget> {
        private final Function<String, E> parser;
        private final BiFunction<E, Map<StyleType<?,StyleTarget>, Style<?, StyleTarget>>, Boolean> validator;

        public EnumStyleType(
                Function<String, E> parser,
                BiFunction<E, Map<StyleType<?,StyleTarget>, Style<?, StyleTarget>>, Boolean> validator,
                E defaultValue
        ) {
            this.parser = parser;
            this.validator = validator;
            EMPTY = create(defaultValue);
        }

        public final EnumStyle<E> EMPTY;

        @Override
        public EnumStyle<E> getDefault() {
            return EMPTY;
        }

        @Override
        public EnumStyle<E> create(String string) {
            EnumStyleType<E> type = this;
            return new EnumStyle<E>(parser.apply(string),validator) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return type;
                }
            };
        }

        public EnumStyle<E> create(E value) {
            EnumStyleType<E> type = this;
            return new EnumStyle<E>(value,validator) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return type;
                }
            };
        }

        public static <E extends Enum<E>> EnumStyleType<E> of(
                Function<String, E> parser,
                BiFunction<E, Map<StyleType<?,StyleTarget>, Style<?, StyleTarget>>, Boolean> validator,
                E defaultValue
        ) {
            return new EnumStyleType<>(parser, validator, defaultValue);
        }
    }
}