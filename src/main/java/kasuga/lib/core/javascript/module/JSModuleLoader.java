package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptModuleLoader;

import java.util.ArrayList;
import java.util.List;

public class JSModuleLoader implements JavascriptModuleLoader {
    JSModuleLoader parent;
    List<JavascriptModuleLoader> loaders = new ArrayList<>();

    public JSModuleLoader(JSModuleLoader parent){
        this.parent = parent;
    }

    public void prepend(JavascriptModuleLoader moduleLoader){
        loaders.add(0, moduleLoader);
    }

    public void add(JavascriptModuleLoader moduleLoader){
        loaders.add(moduleLoader);
    }

    @Override
    public JavascriptEngineModule load(JavascriptEngineContext engineContext, String name, JavascriptEngineModule source) {
        for (JavascriptModuleLoader loader : this.loaders) {
            JavascriptEngineModule loadResult = loader.load(engineContext, name, source);
            if(loadResult != null){
                return loadResult;
            }
        }
        if(parent != null){
            JavascriptEngineModule module = parent.load(engineContext, name, source);
            if(module != null){
                return module;
            }
        }
        return null;
    }

    public void register(JavascriptModuleLoader moduleLoader) {
        add(moduleLoader);
    }
}
