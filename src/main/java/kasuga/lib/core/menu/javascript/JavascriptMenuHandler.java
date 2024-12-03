package kasuga.lib.core.menu.javascript;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;

public class JavascriptMenuHandler {
    private JavascriptEngineContext context;
    private JavascriptValue handle;
    JavascriptMenuHandler(JavascriptEngineContext context,JavascriptValue handle){
        this.handle = handle;
        this.context = context;
    }
    public Runnable open(JavascriptMenuHandle handle){
        JavascriptValue handleValue = context.asValue(handle);
        JavascriptValue value = this.handle.execute(handleValue);
        if(value.canExecute()){
            value.pin();
        }
        return ()->{
            handle.dispatchEvent("close");
            if(value.canExecute()){
                value.executeVoid();
                value.unpin();
            }
        };
    }

    public static JavascriptMenuHandler ofExecutable(JavascriptEngineContext context, JavascriptValue value){
        if(value.canExecute()){
            return new JavascriptMenuHandler(context,value);
        }
        throw new IllegalStateException("Value is not executable");
    }
}
