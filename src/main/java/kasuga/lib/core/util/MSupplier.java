package kasuga.lib.core.util;

import java.util.function.Supplier;

public interface MSupplier<T> extends Supplier<Supplier<T>> {}