package kasuga.lib.core.util;

import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

public class LazyRecomputable<T> {
    private final Supplier<T> supplier;
    private T cachedValue;
    private boolean isCached;

    public LazyRecomputable(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyRecomputable<T> of(Supplier<T> supplier){
        return new LazyRecomputable<>(supplier);
    }

    public void clear(){
        this.isCached = false;
        this.cachedValue = null;
    }

    public T get(){
        if(!isCached){
            this.cachedValue = supplier.get();
            this.isCached = true;
        }
        return this.cachedValue;
    }
}
