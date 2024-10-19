package kasuga.lib.core.javascript.engine;


import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.util.data_type.Pair;

import java.io.InputStream;
import java.util.List;

public interface JavascriptEngineContext {
    void loadModule(String moduleName);

    JavascriptValue asValue(Object object);

    JavascriptModuleScope getModuleScope();

    JavascriptEngineModule compileModuleFromSource(NodePackage packageTarget, String fileName, String dirName, InputStream stream);

    JavascriptEngineModule compileNativeModule(Object target, String moduleName);

    JavascriptContext getContext();

    void loadModuleWithParent(String moduleName, JavascriptEngineModule parent);
}
