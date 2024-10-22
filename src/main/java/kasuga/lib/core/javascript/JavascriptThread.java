package kasuga.lib.core.javascript;

import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.client.animation.neo_neo.key_frame.KeyFrameHolder;
import kasuga.lib.core.javascript.engine.ScriptEngine;

import java.util.HashMap;
import java.util.Map;

public class JavascriptThread extends SynchronizedThread{
    private final JavascriptThreadGroup threadGroup;
    private final Map<Object,JavascriptContext> contexts = new HashMap<>();
    public ScriptEngine scriptEngine;

    public ContextModuleLoader contextModuleLoader;

    public JavascriptThread(JavascriptThreadGroup javascriptThreadGroup, Object target, String description) {
        super("Javascript Thread - " + description);
        this.threadGroup = javascriptThreadGroup;
        this.scriptEngine = javascriptThreadGroup.getScriptEngine();
        this.contextModuleLoader = new ContextModuleLoader(javascriptThreadGroup.getModuleLoader());
    }

    @Override
    protected void tick() {
        this.contexts.values().forEach(JavascriptContext::tick);
    }

    @Override
    protected void beforeStop() {
        this.contexts.values().forEach(JavascriptContext::close);
        threadGroup.onTerminate(this);
    }

    public JavascriptContext createContext(Object target,String name){
        return contexts.computeIfAbsent(target, o -> new JavascriptContext(name, this));
    }

    public void closeContext(Object obj) {
        JavascriptContext context = contexts.remove(obj);
        if(context != null){
            context.close();
        }
    }

    public ContextModuleLoader getContextModuleLoader() {
        return contextModuleLoader;
    }
}
