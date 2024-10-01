package kasuga.lib.core.javascript.registration;

import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.core.util.data_type.Pair;

import java.util.HashSet;
import java.util.Set;

public class RegistrySet<T,C extends Comparable<C>> {
    Set<Pair<T,C>> registry = new HashSet<>();

    LazyRecomputable<T> present = LazyRecomputable.of(()->{
        C currentMaxPriority = null;
        T currentItem = null;
        for (Pair<T, C> registryItem : registry) {
            if(currentMaxPriority == null){
                currentItem = registryItem.getFirst();
                currentMaxPriority = registryItem.getSecond();
            }
            if(registryItem.getSecond().compareTo(currentMaxPriority) > 0){
                currentItem = registryItem.getFirst();
                currentMaxPriority = registryItem.getSecond();
            }
        }
        return currentItem;
    });

    public T getPresent(){
        return present.get();
    }

    public void register(T item,C priority){
        registry.add(Pair.of(item,priority));
        present.clear();
    }

    public void remove(T item,C priority){
        registry.remove(Pair.of(item,priority));
        present.clear();
    }

    public boolean empty(){
        return registry.isEmpty();
    }
}
