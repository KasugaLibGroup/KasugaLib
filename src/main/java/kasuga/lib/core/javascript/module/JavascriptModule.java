package kasuga.lib.core.javascript.module;

import kasuga.lib.core.addons.node.PackageReader;
import kasuga.lib.core.javascript.JavascriptContext;
import org.graalvm.polyglot.Value;

import java.util.concurrent.FutureTask;

public class JavascriptModule {
    private final JavascriptContext context;

    public JavascriptModule(JavascriptContext context) {
        this.context = context;
    }
    public JavascriptContext getContext(){
        return context;
    }

    public Value get(){
        return Value.asValue(this);
    }
}
