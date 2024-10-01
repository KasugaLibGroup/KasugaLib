package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class EnumStyle<E> extends Style<E, StyleTarget> {

    public final String original;
    public final E value;
    private final BiFunction<E, Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>>, Boolean> validator;
    private StyleTarget target;

    public EnumStyle(
            E value,
            Function<E, StyleTarget> targetSupplier,
            BiFunction<E, Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>>, Boolean> validator
    ) {
        this.value = value;
        this.original = value.toString();
        this.target = targetSupplier.apply(value);
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

    @Override
    public StyleTarget getTarget() {
        return target;
    }

    public static class EnumStyleType<E> implements StyleType<EnumStyle<E>, StyleTarget> {
        private final Function<String, E> parser;
        private final BiFunction<E, Map<StyleType<?,StyleTarget>, Style<?, StyleTarget>>, Boolean> validator;
        private final Function<E, StyleTarget> targetSupplier;

        public EnumStyleType(
                Function<String, E> parser,
                BiFunction<E, Map<StyleType<?,StyleTarget>, Style<?, StyleTarget>>, Boolean> validator,
                Function<E, StyleTarget> targetSupplier,
                E defaultValue
        ) {
            this.parser = parser;
            this.targetSupplier = targetSupplier;
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
            return new EnumStyle<E>(parser.apply(string),targetSupplier,validator) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return type;
                }
            };
        }

        public EnumStyle<E> create(E value) {
            EnumStyleType<E> type = this;
            return new EnumStyle<E>(value,targetSupplier,validator) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return type;
                }
            };
        }

        public static <E extends Enum<E>> EnumStyleType<E> of(
                Function<String, E> parser,
                BiFunction<E, Map<StyleType<?,StyleTarget>, Style<?, StyleTarget>>, Boolean> validator,
                Function<E, StyleTarget> target,
                E defaultValue
        ) {
            return new EnumStyleType<>(parser, validator, target, defaultValue);
        }
    }
}