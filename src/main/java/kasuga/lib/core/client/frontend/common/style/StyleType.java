package kasuga.lib.core.client.frontend.common.style;

import java.util.function.Function;

public interface StyleType<T extends Style<?,R>,R> {
    public T getDefault();
    public T create(String string);

    public static <T extends Style<?, R>,R> StyleType<T,R> of(Function<String,T> creator, String defaultValue){
        T defaultStyle = creator.apply(defaultValue);
        return new StyleType<T, R>() {
            @Override
            public T getDefault() {
                return defaultStyle;
            }

            @Override
            public T create(String string) {
                return creator.apply(string);
            }
        };
    }
}
