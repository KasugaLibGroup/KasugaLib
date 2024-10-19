package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class JavetContext implements JavascriptEngineContext {
    V8Runtime runtime;

    JavetModuleAPI javetModuleAPI;

    @Override
    public void loadModule(String moduleName) {
        try {
            runtime.getV8ModuleResolver().resolve(runtime,moduleName,null).execute();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadModuleWithParent(String moduleName, JavascriptEngineModule parent) {
        this.javetModuleAPI.resolve(runtime, moduleName, parent);
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
        return null;
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
            return new JavetJavascriptModule(
                    runtime.getExecutor(textBuilder.toString()).compileV8Module(),
                    packageTarget,
                    dirName,
                    fileName
            );
        }catch (JavetException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public JavetJavascriptModule compileNativeModule(Object target, String moduleName) {
        try{
            return new JavetJavascriptModule(
                    runtime.createV8Module(null, runtime.getConverter().toV8Value(runtime, target)),
                    null,
                    "native@"+moduleName,
                    "native@"+moduleName
            );
        }catch (JavetException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public JavascriptContext getContext() {
        return null;
    }
}
