package kasuga.lib.core.javascript.utils;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalUtil {
    @SafeVarargs
    public static <T> Optional<T> firstNotEmpty(Supplier<Optional<T>> ...suppliers){
        for (Supplier<Optional<T>> supplier : suppliers) {
            Optional<T> optional = supplier.get();
            if(optional.isPresent())
                return optional;
        }
        return Optional.empty();
    }
}
