package kasuga.lib.core.client.frontend.dom.event;

import org.graalvm.polyglot.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class EventEmitter {

    HashMap<String, Set<Consumer<Value>>> listeners = new HashMap<>();

    public UnsubscribeHandler subscribe(String eventName, Consumer<Value> consumer){
        listeners.computeIfAbsent(eventName,(v)->new HashSet<>())
                .add(consumer);
        return ()->{
            unsubscribe(eventName, consumer);
        };
    }

    public void unsubscribe(String eventName, Consumer<Value> consumer){
        listeners.computeIfPresent(eventName,(name,data)->{
           data.remove(consumer);
           return data.isEmpty() ? null : data;
        });
    }

    public void dispatchEvent(String eventName, Value event){
        Set<Consumer<Value>> consumers = this.listeners.get(eventName);
        if(consumers == null)
            return;
        for (Consumer<Value> consumer : consumers) {
            consumer.accept(event);
        }
    }

    public static interface UnsubscribeHandler{
        public void unsubscribe();
    }
}
