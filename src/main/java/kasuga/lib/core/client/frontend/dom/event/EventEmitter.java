package kasuga.lib.core.client.frontend.dom.event;

import org.graalvm.polyglot.Value;

import java.util.*;
import java.util.function.Consumer;

public class EventEmitter {

    HashMap<String, Set<Consumer<Object>>> listeners = new HashMap<>();
    HashMap<String, Set<Value>> functionalListeners = new HashMap<>();

    public UnsubscribeHandler subscribe(String eventName, Consumer<Object> consumer){
        listeners.computeIfAbsent(eventName,(v)->new HashSet<>())
                .add(consumer);
        return ()->{
            unsubscribe(eventName, consumer);
        };
    }

    public void unsubscribe(String eventName, Consumer<Object> consumer){
        listeners.computeIfPresent(eventName,(name,data)->{
           data.remove(consumer);
           return data.isEmpty() ? null : data;
        });
    }

    public void subscribe(String eventName, Value consumer){
        functionalListeners.computeIfAbsent(eventName,(v)->new HashSet<>())
                .add(consumer);
    }

    public void unsubscribe(String eventName, Value consumer){
        functionalListeners.computeIfPresent(eventName,(name,data)->{
            data.remove(consumer);
            return data.isEmpty() ? null : data;
        });
    }

    public void dispatchEvent(String eventName, Object event){
        Set<Consumer<Object>> consumers = this.listeners.get(eventName);
        if(consumers != null){
            List<Consumer<Object>> temporaryConsumer = new ArrayList<>(consumers);
            for (Consumer<Object> consumer : temporaryConsumer) {
                consumer.accept(event);
            }
        }

        if(this.functionalListeners.containsKey(eventName)){
            ArrayList<Value> functionalConsumers =
                    new ArrayList<>(this.functionalListeners.get(eventName));
            for (Value consumer : functionalConsumers) {
                consumer.executeVoid(event);
            }
        }
    }

    public static interface UnsubscribeHandler{
        public void unsubscribe();
    }
}
