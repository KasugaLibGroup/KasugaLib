package kasuga.lib.core.javascript.prebuilt;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;

public abstract class PrebuiltModule {
    protected PrebuiltModule(JavascriptContext runtime){
        if(isTickable()){
            runtime.registerTickable(this::tick);
        }
        runtime.collectEffect(this::close);
    }

    protected boolean isTickable(){
        return false;
    }

    protected void tick(){}
    protected void close() {}
}
