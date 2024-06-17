package kasuga.lib.core.util;

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

    public static <T> PredictableLazyRecomputable<T> predictable(Supplier<T> supplier, Supplier<Boolean> predict){
        return new PredictableLazyRecomputable<>(supplier,predict);
    }

    public static class PredictableLazyRecomputable<T> extends LazyRecomputable<T>{
        private final Supplier<Boolean> predict;

        public PredictableLazyRecomputable(Supplier<T> supplier, Supplier<Boolean> predict) {
            super(supplier);
            this.predict = predict;
        }

        @Override
        public T get() {
            if(!predict.get())
                this.clear();
            return super.get();
        }
    }
}
