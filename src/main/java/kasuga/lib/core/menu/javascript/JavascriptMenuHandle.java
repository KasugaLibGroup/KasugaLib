package kasuga.lib.core.menu.javascript;

import kasuga.lib.core.client.frontend.dom.event.EventEmitter;
import kasuga.lib.core.javascript.engine.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;

import java.util.HashMap;

public class JavascriptMenuHandle {
    EventEmitter emitter = new EventEmitter();

    HashMap<String, Object> nativeObjects = new HashMap<>();

    @HostAccess.Export
    public void addEventListener(String eventName, JavascriptValue callback){
        if(!callback.canExecute())
            throw new IllegalArgumentException("Callback must be a function");
        emitter.subscribe(eventName, callback);
    }

    @HostAccess.Export
    public void removeEventListener(String eventName, JavascriptValue callback){
        emitter.unsubscribe(eventName, callback);
    }

    public void dispatchEvent(String eventName, Object... args){
        emitter.dispatchEvent(eventName, args);
    }

    @HostAccess.Export
    public Object inject(String key){
        return nativeObjects.get(key);
    }
}
