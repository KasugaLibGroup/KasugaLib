package kasuga.lib.core.client.frontend.common.style;

import kasuga.lib.core.util.Callback;
import org.graalvm.polyglot.HostAccess;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class StyleList<R> {

    private final StyleRegistry<R> styleRegistry;
    public final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

    public StyleList(StyleRegistry<R> registry){
        this.styleRegistry = registry;
    }

    protected List<Style<?,R>> styles = new ArrayList<>();

    protected Map<StyleType<?,R>,Style<?,R>> cache = new HashMap<>();

    public void freshCache(){
        this.cache.clear();
        freshCache(null);
        setHasNewStyle();
    }
    public int freshCache(StyleType<?,R> type){
        Lock cacheWriteLock = cacheLock.writeLock();
        cacheWriteLock.lock();
        int i = 0;
        for (Style<?,R> style : styles) {
            if(style.getType() != type)
                continue;
            if(style.isValid(cache)){
                cache.put(style.getType(),style);
                // waiting.put(style.getType(),style);
                i++;
            }
        }
        cacheWriteLock.unlock();
        return i;
    }

    public void addStyle(Style<?,R> style){
        StyleType<?,R> type = style.getType();
        styles.add(style);
        if(cache.get(type) == style){
            return;
        }
        Lock cacheWriteLock = cacheLock.writeLock();

        if(style.isValid(cache)){
            cacheWriteLock.lock();
            cache.put(style.getType(),style);
            cacheWriteLock.unlock();
        }
        setHasNewStyle();
    }


    public void removeStyle(Style<?,R> style){
        StyleType<?,R> type = style.getType();
        if(cache.get(type) != style){
            styles.remove(style);
        }
        styles.remove(style);
        Lock cacheWriteLock = cacheLock.writeLock();
        cacheWriteLock.lock();
        int i = freshCache(type);
        if(i == 0){
            cache.put(type, type.getDefault());
        }
        this.cache.remove(type);
        cacheWriteLock.unlock();
        setHasNewStyle();
    }

    /* public void apply(R node){
        waiting.forEach((styleType, style)->{
            style.apply(node);
        });
        waiting.clear();
        setHasNewStyle();
    } */

    public String toString(){
        cacheLock.readLock().lock();
        StringBuilder sb = new StringBuilder();
        for (Style<?,R> style : this.styles) {
            if(!sb.isEmpty()){
                sb.append(';');
            }
            sb.append(style.toString());
        }
        cacheLock.readLock().unlock();
        return sb.toString();
    }

    public void decode(String style) {
        cacheLock.writeLock().lock();
        Stack<StyleStates.ReadState> readStateStack = new Stack<>();
        StyleStates.PartState partState = StyleStates.PartState.ATTRIBUTE;
        StringBuilder current = new StringBuilder();
        String key = "";
        for(int i=0;i<style.length();i++){
            char chr = style.charAt(i);
            switch (chr){
                case '\\':
                    if(partState == StyleStates.PartState.ATTRIBUTE){
                        throw new IllegalStateException("ESCAPE CHARACTER \"\\\" IS NOT ALLOWED IN ATTRIBUTE AREA");
                    }
                    current.append(chr);
                    break;
                case ':':
                    if(partState == StyleStates.PartState.ATTRIBUTE){
                        partState = StyleStates.PartState.VALUE;
                    }
                    key = current.toString();
                    current = new StringBuilder();
                    break;
                case ';':
                    if(partState == StyleStates.PartState.VALUE){
                        StyleType<?,R> styleType = styleRegistry.getStyle(key);
                        if(styleType != null){
                            this.addStyle(styleType.create(current.toString()));
                        }
                        key = "";
                        current = new StringBuilder();
                    }
                    break;
                default:
                    current.append(chr);
            }
        }
        cacheLock.writeLock().unlock();
    }

    @SuppressWarnings("unchecked")
    public <T extends Style<?,R>> T get(StyleType<T,R> type) {
        return (T) this.cache.get(type);
    }

    public void clear(){
        cacheLock.writeLock().lock();
        this.styles.clear();
        this.cache.clear();
        styleUpdate.clear();
        cacheLock.writeLock().unlock();
        setHasNewStyle();
    }


    protected WeakHashMap<Object, Boolean> styleUpdate = new WeakHashMap<>();

    public boolean hasNewStyle(Object target){
        return styleUpdate.getOrDefault(target,true);
    }

    public Callback callback = null;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void notifyUpdate(){
        setHasNewStyle();
    }

    public void resetNewStyle(Object target){
        styleUpdate.put(target,false);
    }

    public Collection<Style<?, R>> getCachedStyles() {
        return cache.values();
    }

    public void forEachCacheStyle(Consumer<Style<?,R>> consumer){
        cacheLock.readLock().lock();
        for (Style<?, R> value : cache.values()) {
            consumer.accept(value);
        }
        cacheLock.readLock().unlock();
    }

    private void setHasNewStyle() {
        styleUpdate.clear();

        if(this.callback != null)
            this.callback.execute();
    }

    @HostAccess.Export
    public void setStyle(String name, String value){
        removeStyle(name);
        StyleType<?,R> type = styleRegistry.getStyle(name);
        if(type == null)
            return;
        this.addStyle(type.create(value));
    }

    @HostAccess.Export
    public void removeStyle(String name){
        StyleType<?,R> type = styleRegistry.getStyle(name);
        if(type == null)
            return;
        Style<?,R> current = cache.get(type);
        while(current != null){
            removeStyle(current);
            current = cache.get(type);
        }
    }
}
