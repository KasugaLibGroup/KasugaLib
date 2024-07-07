package kasuga.lib.core.javascript;

import kasuga.lib.core.util.Callback;

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
        callbackRef[0] = callback;
        Callback actualCallback = ()->{
            try{
                callbackRef[0].execute();
            }catch (Exception e){
                e.printStackTrace();
            }
            effectCallbacks.remove(callbackRef[0]);
        };
        effectCallbacks.add(actualCallback);
        return actualCallback;
    }

    public void close(){
        effectCallbacks.forEach(Callback::execute);
    }
}
