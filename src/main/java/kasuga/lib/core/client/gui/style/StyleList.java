package kasuga.lib.core.client.gui.style;

import kasuga.lib.core.client.gui.components.MultipleLocator;
import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.layout.yoga.YogaNode;

import java.util.*;

public class StyleList {
    protected List<Style<?>> styles = new ArrayList<>();

    protected HashMap<StyleType<?>,Style<?>> cache = new HashMap<>();

    protected HashMap<StyleType<?>,Style<?>> waitingNode = new HashMap<>();

    protected HashMap<StyleType<?>,Style<?>> waitingLocator = new HashMap<>();

    public void freshCache(){
        this.cache.clear();
        this.waitingNode.clear();
        freshCache(null);
    }
    public int freshCache(StyleType<?> type){
        int i = 0;
        for (Style<?> style : styles) {
            if(style.getType() != type)
                continue;
            if(style.isValid(cache)){
                cache.put(style.getType(),style);
                waitingNode.put(style.getType(),style);
                i++;
            }
        }
        return i;
    }

    public void addStyle(Style<?> style){
        StyleType<?> type = style.getType();
        styles.add(style);
        if(cache.get(type) == style){
            return;
        }
        if(style.isValid(cache)){
            cache.put(style.getType(),style);
            waitingNode.put(style.getType(),style);
        }
    }

    public void removeStyle(Style<?> style){
        StyleType<?> type = style.getType();
        if(cache.get(type) != style){
            styles.remove(style);
        }
        styles.remove(style);
        this.cache.remove(type);
        int i = freshCache(type);
        if(i == 0)
            waitingNode.put(type,type.getDefault());
    }

    public void apply(Node node){
        waitingNode.forEach((styleType, style)->{
            style.apply(node);
            node.getLocator().applyStyleInstant(styleType,style);
        });
        waitingNode.clear();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (Style<?> style : this.styles) {
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
                        StyleType<?> styleType = StyleRegistry.getStyle(key);
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
    public <T extends Style<?>> T get(StyleType<T> type) {
        return (T) this.cache.get(type);
    }
}
