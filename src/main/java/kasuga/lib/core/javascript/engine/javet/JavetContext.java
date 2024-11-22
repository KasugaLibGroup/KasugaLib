package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.enums.JavetPromiseRejectEvent;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.BaseJavetDirectCallableInterceptor;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.IV8Native;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Module;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValuePromise;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class JavetContext implements JavascriptEngineContext {
    private final JavascriptContext context;
    V8Runtime runtime;
    JavetModuleAPI moduleAPI;

    JavetContext(JavascriptContext context) throws JavetException {
        this.context = context;
        runtime = V8Host.getV8Instance().createV8Runtime();
        JavetStandardConsoleInterceptor consoleInterceptor = new JavetStandardConsoleInterceptor(runtime);
        consoleInterceptor.register(runtime.getGlobalObject());
        moduleAPI = new JavetModuleAPI(runtime,this, context.getModuleLoader());
        runtime.setConverter(new JavetKasugaConverter(runtime));
        runtime.setPromiseRejectCallback((event, promise, value)->{
            if(event.getCode() == 0){
                System.err.println("Error" + event.getName());
            }
        });
    }

    @Override
    public void loadModule(String moduleName) {
        // this.moduleAPI.requireModule(runtime,moduleName, null);
        this.moduleAPI.getRequireFunction(null).execute(moduleName);
    }

    @Override
    public JavascriptValue asValue(Object object) {
        try {
            return new JavetJavascriptValue(runtime.getConverter().toV8Value(runtime,object), runtime);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public JavascriptModuleScope getModuleScope() {
        return this.getContext().getModuleScope();
    }

    @Override
    public JavetJavascriptModule compileModuleFromSource(NodePackage packageTarget, String fileName, String dirName, InputStream stream) {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }catch (IOException e){
            throw new RuntimeException("Failed to read");
        }
        try{
            UUID uniqueId = UUID.randomUUID();
            String temporyModuleName = fileName;

            /* V8ValueFunction module =
                    runtime
                            .getExecutor(textBuilder.toString())
                            .setResourceName(temporyModuleName);*/

            V8ValueFunction module =
                    runtime.getExecutor(textBuilder.toString())
                            .setResourceName(fileName)
                            .compileV8ValueFunction(new String[]{"require", "exports", "module"});
            // module.setWeak();

            return new JavetJavascriptModule(
                    module,
                    packageTarget,
                    fileName,
                    dirName,
                    this.context
            );
        }catch (JavetException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public JavetJavascriptModule compileNativeModule(Object target, String moduleName) {
        try{
            V8Value module = runtime.getConverter().toV8Value(runtime, target);
            // module.setWeak();
            return new JavetJavascriptModule(
                    module,
                    "native@"+moduleName
            );
        }catch (JavetException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public JavascriptContext getContext() {
        return context;
    }
}
