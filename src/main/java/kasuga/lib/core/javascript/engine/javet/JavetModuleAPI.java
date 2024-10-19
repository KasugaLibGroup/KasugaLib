package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IV8ModuleResolver;
import com.caoccao.javet.interop.callback.JavetBuiltInModuleResolver;
import com.caoccao.javet.values.reference.IV8Module;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptModuleLoader;
import kasuga.lib.core.util.data_type.Pair;

import java.util.HashMap;
import java.util.WeakHashMap;

public class JavetModuleAPI implements IV8ModuleResolver {

    private final JavascriptModuleLoader moduleLoader;
    private final JavascriptEngineContext engineContext;
    HashMap<Pair<JavascriptEngineModule, String>, IV8Module> moduleCache;

    HashMap<IV8Module, JavetJavascriptModule> sourceModule;

    JavetModuleAPI(JavascriptEngineContext engineContext, JavascriptModuleLoader moduleLoader){
        this.engineContext = engineContext;
        this.moduleLoader = moduleLoader;
    }

    @Override
    public IV8Module resolve(V8Runtime v8Runtime, String s, IV8Module iv8Module) throws JavetException {
        JavetJavascriptModule module = null;
        if(sourceModule.containsValue(iv8Module))
            module = sourceModule.get(iv8Module);
        return resolve(v8Runtime, s, module);
    }

    public IV8Module resolve(V8Runtime runtime, String moduleName, JavascriptEngineModule parent) {
        JavascriptEngineModule newModule = this.moduleLoader.load(engineContext, moduleName, parent);
        if(!(newModule instanceof JavetJavascriptModule javetJavascriptModule)){
            throw new RuntimeException("Invalid Module.");
        }
        if(javetJavascriptModule.shouldCache())
            this.moduleCache.put(Pair.of(parent, moduleName), javetJavascriptModule.getModule());
        this.sourceModule.put(javetJavascriptModule.getModule(), javetJavascriptModule);
        return javetJavascriptModule.getModule();
    }
}
