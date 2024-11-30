package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8Module;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.addons.node.AssetReader;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.AbstractJavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptValue;

import java.util.function.BiFunction;

public class JavetJavascriptModule extends AbstractJavascriptEngineModule {
    JavascriptContext context;
    V8Value rawModule;
    V8ValueFunction module;
    String absolutePath;
    NodePackage nodePackage;
    String directoryName;

    AssetReader assetReader;

    public JavetJavascriptModule(
            V8ValueFunction module,
            NodePackage nodePackage,
            String absolutePath,
            String directoryName,
            JavascriptContext context
    ){
      this(module, nodePackage, nodePackage!= null ? new AssetReader(
              directoryName,
              context,
              nodePackage.reader,
              KasugaLib.STACKS.JAVASCRIPT.ASSETS.get(),
              nodePackage.minecraft.assetsFolder()
      ): null, absolutePath, directoryName, context);
    }
    public JavetJavascriptModule(
            V8ValueFunction module,
            NodePackage nodePackage,
            AssetReader reader,
            String absolutePath,
            String directoryName,
            JavascriptContext context
    ){
        this.module = module;
        this.absolutePath = absolutePath;
        this.nodePackage = nodePackage;
        this.directoryName = directoryName;
        this.context = context;
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
                return JavetValue.weakClone(rawModule);
            }
            V8ValueObject moduleObject = runtime.createV8ValueObject();
            V8ValueObject exportsObject = runtime.createV8ValueObject();
            V8ValueObject thisObject = runtime.createV8ValueObject();
            moduleObject.set("require", requireFunction);
            moduleObject.set("exports", exportsObject);
            if(KasugaLib.STACKS.JAVASCRIPT.ASSETS.isPresent() && this.context != null){
                moduleObject.set("asset", assetReader);
            }
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

    @Override
    public void setAssetReader(AssetReader assetReader) {
        this.assetReader = assetReader;
    }
}
