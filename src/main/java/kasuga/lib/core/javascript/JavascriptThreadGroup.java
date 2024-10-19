package kasuga.lib.core.javascript;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JavascriptThreadGroup {

    private final String name;
    Map<Object, JavascriptThread> threads = new HashMap<>();

    Set<JavascriptThread> terminating = new HashSet<>();

    JavascriptThreadGroup parent;

    ThreadGroup threadGroup;

    Set<JavascriptThreadGroup> children = new HashSet<>();

    ContextModuleLoader moduleLoader;

    public JavascriptThreadGroup(String name){
        this.threadGroup = new ThreadGroup(name);
        parent = null;
        this.name = name;
        moduleLoader = new ContextModuleLoader(parent.moduleLoader);
    }

    public JavascriptThreadGroup(JavascriptThreadGroup parent, String name){
        this.threadGroup = new ThreadGroup(parent.threadGroup, name);
        this.parent = parent;
        this.name = name;
        moduleLoader = new ContextModuleLoader(parent.moduleLoader);
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
        for (JavascriptThread thread : threads.values()) {
            thread.shouldShutdown.set(true);
        }
        terminating.addAll(threads.values());
        threads.clear();
        threadGroup.interrupt();
    }

    public void terminate(Object target){
        JavascriptThread thread = this.threads.get(target);

        if(thread != null) {
            thread.shouldShutdown.set(true);
            this.threads.remove(thread);
            terminating.add(thread);
            thread.interrupt();
        }

        for (JavascriptThreadGroup child : this.children) {
            child.terminate(target);
        }
    }

    protected void onTerminate(JavascriptThread thread){
        terminating.remove(thread);
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

    public ContextModuleLoader getModuleLoader() {
        return moduleLoader;
    }
}
