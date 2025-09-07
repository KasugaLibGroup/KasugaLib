package kasuga.lib.core.javascript.engine;

import kasuga.lib.core.javascript.JavascriptContext;

public interface ScriptEngine {

    JavascriptEngineContext createInstance(JavascriptContext context);
    void init();
}
