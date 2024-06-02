package kasuga.lib.core.client.frontend.common.style;

import java.util.*;

public class StyleList<R> {

    private final StyleRegistry<R> styleRegistry;

    public StyleList(StyleRegistry<R> registry){
        this.styleRegistry = registry;
    }

    protected List<Style<?,R>> styles = new ArrayList<>();

    protected Map<StyleType<?,R>,Style<?,R>> cache = new HashMap<>();

    public void freshCache(){
        this.cache.clear();
        // this.waiting.clear();
        freshCache(null);
        setHasNewStyle();
    }
    public int freshCache(StyleType<?,R> type){
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
        return i;
    }

    public void addStyle(Style<?,R> style){
        StyleType<?,R> type = style.getType();
        styles.add(style);
        if(cache.get(type) == style){
            return;
        }
        if(style.isValid(cache)){
            cache.put(style.getType(),style);
            // waiting.put(style.getType(),style);
        }
    }

    public void removeStyle(Style<?,R> style){
        StyleType<?,R> type = style.getType();
        if(cache.get(type) != style){
            styles.remove(style);
        }
        styles.remove(style);
        this.cache.remove(type);
        int i = freshCache(type);
        if(i == 0){
            cache.put(type, type.getDefault());
        }
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
        StringBuilder sb = new StringBuilder();
        for (Style<?,R> style : this.styles) {
            if(!sb.isEmpty()){
                sb.append(';');
            }
            sb.append(style.toString());
        }
        return sb.toString();
    }

    public void decode(String style) {
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
                            this.addStyle(styleType.create(styleType.toString()));
                        }
                        key = "";
                        current = new StringBuilder();
                    }
                    break;
                default:
                    current.append(chr);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Style<?,R>> T get(StyleType<T,R> type) {
        return (T) this.cache.get(type);
    }

    public void clear(){
        this.styles.clear();
        this.cache.clear();
        styleUpdate.clear();
    }


    protected WeakHashMap<Object, Boolean> styleUpdate = new WeakHashMap<>();

    public boolean hasNewStyle(Object target){
        return styleUpdate.getOrDefault(target,true);
    }

    public void resetNewStyle(Object target){
        styleUpdate.put(target,false);
    }

    public Collection<Style<?, R>> getCachedStyles() {
        return cache.values();
    }

    private void setHasNewStyle() {
        styleUpdate.replaceAll((s, v) -> true);
    }
}
