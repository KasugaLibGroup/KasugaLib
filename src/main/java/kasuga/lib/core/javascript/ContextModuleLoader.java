package kasuga.lib.core.javascript;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.module.JavascriptModule;
import kasuga.lib.core.javascript.module.JavascriptModuleScope;
import kasuga.lib.core.javascript.module.RegistryLoader;

import java.util.Optional;

public class ContextModuleLoader {
    protected JavascriptModuleScope scope;
    protected RegistryLoader loader;

    public ContextModuleLoader(JavascriptModuleScope scope, RegistryLoader loader){
        this.scope = scope;
        this.loader = loader;
    }

    public ContextModuleLoader(){
        this.scope = new JavascriptModuleScope();
        this.loader = new RegistryLoader();
    }

    public ContextModuleLoader(ContextModuleLoader parent){
        this.scope = new JavascriptModuleScope(parent.scope);
        this.loader = new RegistryLoader(parent.loader);
    }

    public Optional<JavascriptModule> load(JavascriptModule source, String target) {
        return this.loader.load(source, target);
    }

    public void registerPackage(NodePackage nodePackage){
        this.scope.packages.add(nodePackage);
    }

    public void unregisterPackage(NodePackage nodePackage){
        this.scope.packages.remove(nodePackage);
    }

    public RegistryLoader getLoader() {
        return loader;
    }
}
