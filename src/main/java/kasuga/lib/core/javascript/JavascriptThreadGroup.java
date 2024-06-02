package kasuga.lib.core.javascript;

import kasuga.lib.core.javascript.module.CommonJSModuleLoader;
import kasuga.lib.core.javascript.module.ModuleLoader;
import kasuga.lib.core.javascript.module.ModuleLoaderRegistry;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModuleLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JavascriptThreadGroup {

    private final String name;
    Map<Object,JavascriptThread> threads = new HashMap<>();
    JavascriptThreadGroup parent;

    ThreadGroup threadGroup;

    Set<JavascriptThreadGroup> children = new HashSet<>();

    ModuleLoaderRegistry loaderRegistry = new ModuleLoaderRegistry();
    ModuleLoaderRegistry innerLoaderRegistry = new ModuleLoaderRegistry();

    protected final CommonJSModuleLoader moduleLoader;

    public JavascriptThreadGroup(String name){
        this.threadGroup = new ThreadGroup(name);
        this.innerLoaderRegistry.register(new PrebuiltModuleLoader());
        this.innerLoaderRegistry.register(loaderRegistry);
        moduleLoader = new CommonJSModuleLoader();
        this.innerLoaderRegistry.register(moduleLoader);
        parent = null;
        this.name = name;
    }

    public JavascriptThreadGroup(JavascriptThreadGroup javascriptThreadGroup, String name){
        this.threadGroup = new ThreadGroup(javascriptThreadGroup.threadGroup, name);
        this.parent = javascriptThreadGroup;
        this.innerLoaderRegistry.register(loaderRegistry);
        this.innerLoaderRegistry.register(parent.innerLoaderRegistry);
        this.name = name;
        moduleLoader = null;
    }

    public JavascriptThread getOrCreate(Object target, String description){
        if(this.threads.containsKey(target))
            return threads.get(target);
        JavascriptThread thread = new JavascriptThread(this,target,description);
        this.threads.put(target, thread);
        thread.start();
        return thread;
    }

    public void terminate(){
        threadGroup.interrupt();
    }

    public void terminate(Object target){
        JavascriptThread thread = this.threads.get(target);
        if(thread != null)
            thread.interrupt();

        for (JavascriptThreadGroup child : this.children) {
            child.terminate(target);
        }
    }

    protected void onTerminate(JavascriptThread thread){
        this.threads.remove(thread);
    }

    public void dispatchTick(){
        for (JavascriptThread thread : this.threads.values()) {
            thread.dispatchTick();
        }
        for (JavascriptThreadGroup child : this.children) {
            child.dispatchTick();
        }
    }

    public JavascriptThreadGroup createChild(String name){
        JavascriptThreadGroup threadGroup = new JavascriptThreadGroup(this,name);
        this.children.add(threadGroup);
        return threadGroup;
    }

    public CommonJSModuleLoader getCommonJSModuleLoader(){
        if(moduleLoader != null)
            return moduleLoader;
        return parent.moduleLoader;
    }

    public ModuleLoaderRegistry getLoaderRegistry() {
        return loaderRegistry;
    }
}
