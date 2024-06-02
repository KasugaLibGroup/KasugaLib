package kasuga.lib.core.client.frontend.dom.attribute;

import kasuga.lib.core.util.Callback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttributeMap {

    Map<String, AttributeProxy> specialAttributes = new HashMap<>();

    Map<String, Set<Callback>> callbacks = new HashMap<>();
    Map<String,String> attributes = new HashMap<>();

    public void registerCallback(String attributeName, Callback callback){
        this.callbacks.computeIfAbsent(attributeName,(s)->new HashSet<>()).add(callback);
    }

    public void removeCallback(String attributeName, Callback callback){
        if(!this.callbacks.containsKey(attributeName))
            return;
        Set<Callback> attributeCallbacks = this.callbacks.get(attributeName);
        attributeCallbacks.remove(callback);
        if(attributeCallbacks.isEmpty()){
            this.callbacks.remove(attributeName);
        }
    }

    public void setValue(String attributeName, String attributeValue){
        attributes.put(attributeName, attributeValue);
        if(this.callbacks.containsKey(attributeName))
            for (Callback callback : this.callbacks.get(attributeName)) {
                callback.execute();
            }
    }

    public void set(String attributeName, String attributeValue) {
        if(specialAttributes.containsKey(attributeName)){
            setValue(attributeName,specialAttributes.get(attributeName).set(attributeValue));
            return;
        }
        setValue(attributeName, attributeValue);
    }


    public String get(String attributeName){
        if(specialAttributes.containsKey(attributeName)){
            return specialAttributes.get(attributeName).get();
        }
        return attributes.get(attributeName);
    }

    public String get(String attributeName, String defaultValue){
        return attributes.getOrDefault(attributeName,defaultValue);
    }

    public void registerProxy(String attribute, AttributeProxy proxy){
        specialAttributes.put(attribute, proxy);
    }
}
