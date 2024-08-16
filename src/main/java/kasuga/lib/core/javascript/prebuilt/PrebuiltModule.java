package kasuga.lib.core.javascript.prebuilt;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.JavascriptModule;
import org.graalvm.polyglot.Value;

public abstract class PrebuiltModule extends JavascriptModule {
    protected PrebuiltModule(JavascriptContext runtime){
        super(runtime);
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

    @Override
    public Value get() {
        return Value.asValue(this);
    }
}
