package kasuga.lib.core.client.frontend.font;

import kasuga.lib.core.util.Callback;
import kasuga.lib.core.util.LazyRecomputable;

import java.util.function.Supplier;

public class ExtendableProperty<T> {

    public T thisValue = null;

    public Supplier<T> parentSupplier;

    public Callback updateNotifier;

    public ExtendableProperty(Supplier<T> parentSupplier, Callback updateNotifier){
        this.parentSupplier = parentSupplier;
        this.updateNotifier = updateNotifier;
    }

    public LazyRecomputable<T> cached = LazyRecomputable.of(
            ()-> thisValue == null ? parentSupplier.get() : thisValue
    );

    public void setSize(T value){
        thisValue = value;
        cached.clear();
        this.updateNotifier.execute();
    }

    public void notifyUpdate(){
        cached.clear();
        this.updateNotifier.execute();
    }

    public T get(){
        return cached.get();
    }
}
