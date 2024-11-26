package kasuga.lib.core.util;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class WeakCache<K,V> extends HashMap<K, WeakReference<V>> {
    public void putCache(K key, V value){
        WeakReference<V> ref = this.put(key, new WeakReference<>(value));
    }
    public V getCache(K key){
        WeakReference<V> ref = this.get(key);
        return ref == null ? null : ref.get();
    }

    public void collect(){
        this.entrySet().removeIf(entry -> entry.getValue().get() == null);
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }
}
