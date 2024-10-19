package kasuga.lib.core.javascript;

import java.util.HashMap;
import java.util.Map;

public class JavascriptThread extends SynchronizedThread{
    private final JavascriptThreadGroup threadGroup;
    private final Map<Object,JavascriptContext> contexts = new HashMap<>();

    public JavascriptThread(JavascriptThreadGroup javascriptThreadGroup, Object target, String description) {
        super("Javascript Thread - " + description);
        this.threadGroup = javascriptThreadGroup;
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
}
