package kasuga.lib.core.javascript;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.engine.JavascriptModuleScope;
import kasuga.lib.core.javascript.module.JSModuleLoader;
import kasuga.lib.core.javascript.registration.RegistrationRegistry;

public class ContextModuleLoader {
    private JavascriptModuleScope scope;
    private JSModuleLoader loader;

    public ContextModuleLoader(){
        this.scope = new JavascriptModuleScope(null);
        this.loader = new JSModuleLoader(null);
    }
    public ContextModuleLoader(ContextModuleLoader parent){
        this.scope = new JavascriptModuleScope(parent.scope);
        this.loader = new JSModuleLoader(parent.loader);
    }

    public void registerPackage(NodePackage nodePackage){
        this.scope.packages.add(nodePackage);
    }

    public void unregisterPackage(NodePackage nodePackage){
        this.scope.packages.remove(nodePackage);
    }

    public JSModuleLoader getLoader() {
        return loader;
    }

    public JavascriptModuleScope getScope() {
        return scope;
    }
}
