package kasuga.lib.core.javascript;

import kasuga.lib.core.util.Callback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SideEffectContext {

    Set<Callback> effectCallbacks = new HashSet<>();

    public Callback effect(Supplier<Callback> apply){
        return collect(apply.get());
    }

    public Callback collect(Callback callback){
        Callback[] callbackRef = new Callback[1];
        Callback actualCallback = ()->{
            try{
                callback.execute();
            }catch (Exception e){
                e.printStackTrace();
            }
            remove(callbackRef[0]);
        };
        callbackRef[0] = actualCallback;
        effectCallbacks.add(actualCallback);
        return actualCallback;
    }

    public void remove(Callback callback){
        effectCallbacks.remove(callback);
    }
    public void close(){
        ArrayList<Callback> callbacks = new ArrayList<>(effectCallbacks);

        callbacks.forEach(Callback::execute);
    }
}
