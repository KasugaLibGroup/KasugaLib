package kasuga.lib.core.client.gui.attributes;

import kasuga.lib.core.client.gui.SimpleWidget;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class SimpleAttribute<T> implements Attribute<T> {
    T value;

    SimpleAttribute(T value){
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }


    public static <T> AttributeType<T> of(Function<String,T> parse, Function<T,String> toString, BiConsumer<SimpleWidget,T> consumer){
        return of(parse, toString, consumer, (w,t)->true);
    }
    public static <T> AttributeType<T> of(Function<String,T> parse, Function<T,String> toString, BiConsumer<SimpleWidget,T> consumer, BiFunction<SimpleWidget,T,Boolean> filter){
        Function<T,SimpleAttribute<T>> constructor = (val)->new SimpleAttribute<T>(val) {
            @Override
            public void apply(SimpleWidget widget) {
                consumer.accept(widget,value);
            }

            @Override
            public boolean canApplyTo(SimpleWidget widget) {
                return filter.apply(widget,value);
            }

            @Override
            public String toString() {
                return toString.apply(value);
            }
        };

        return AttributeType.of(
                constructor::apply,
                (s)->constructor.apply(parse.apply(s))
        );
    }
}
