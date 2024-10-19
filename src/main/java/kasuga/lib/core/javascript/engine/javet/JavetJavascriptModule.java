package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8Module;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.engine.AbstractJavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptValue;

public class JavetJavascriptModule extends AbstractJavascriptEngineModule {
    V8Value rawModule;
    V8ValueFunction module;
    String absolutePath;
    NodePackage nodePackage;
    String directoryName;
    public JavetJavascriptModule(
            V8ValueFunction module,
            NodePackage nodePackage,
            String absolutePath,
            String directoryName
    ){
        this.module = module;
        this.absolutePath = absolutePath;
        this.nodePackage = nodePackage;
        this.directoryName = directoryName;
    }

    public JavetJavascriptModule(
            V8Value value,
            String moduleName
    ){
        this.rawModule = value;
        this.absolutePath = this.directoryName = moduleName;
    }
    public V8Value getModule(V8Runtime runtime, JavascriptValue requireFunction){
        try {
            if(this.rawModule != null){
                return this.rawModule.toClone();
            }
            V8ValueObject moduleObject = runtime.createV8ValueObject();
            V8ValueObject exportsObject = runtime.createV8ValueObject();
            V8ValueObject thisObject = runtime.createV8ValueObject();
            moduleObject.set("require", requireFunction);
            moduleObject.set("exports", exportsObject);
            module.callVoid(thisObject, requireFunction, exportsObject, moduleObject);
            return moduleObject.get("exports");
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getAbsoultePath() {
        return absolutePath;
    }

    @Override
    public NodePackage getPackage() {
        return nodePackage;
    }

    @Override
    public String getDirectoryName() {
        return directoryName;
    }
}
