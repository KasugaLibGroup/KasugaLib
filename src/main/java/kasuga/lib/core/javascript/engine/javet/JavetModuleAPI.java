package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8Module;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptModuleLoader;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.data_type.Pair;

import java.util.HashMap;
import java.util.function.Function;

public class JavetModuleAPI {

    private final JavascriptModuleLoader moduleLoader;
    private final JavascriptEngineContext engineContext;
    HashMap<Pair<JavascriptEngineModule, String>, V8Value> moduleCache = new HashMap<>();

    HashMap<V8Value, JavetJavascriptModule> sourceModule = new HashMap<>();
    private V8Runtime runtime;

    JavetModuleAPI(V8Runtime runtime,JavascriptEngineContext engineContext, JavascriptModuleLoader moduleLoader){
        this.engineContext = engineContext;
        this.moduleLoader = moduleLoader;
        this.runtime = runtime;
    }

    public V8Value requireModule(V8Runtime runtime, String moduleName, JavascriptEngineModule parent) {
        JavascriptEngineModule newModule = this.moduleLoader.load(engineContext, moduleName, parent);
        if(!(newModule instanceof JavetJavascriptModule javetJavascriptModule)){
            throw new RuntimeException("Invalid Module.");
        }
        V8Value module = javetJavascriptModule.getModule(runtime, getRequireFunction(javetJavascriptModule));
        if(javetJavascriptModule.shouldCache())
            this.moduleCache.put(Pair.of(parent, moduleName), module);
        this.sourceModule.put(module, javetJavascriptModule);
        return module;
    }

    public JavascriptValue getRequireFunction(JavascriptEngineModule module){
        return this.engineContext.asValue((Function<String,V8Value>)(moduleName)->{
            return requireModule(runtime, moduleName, module);
        });
    }
}
