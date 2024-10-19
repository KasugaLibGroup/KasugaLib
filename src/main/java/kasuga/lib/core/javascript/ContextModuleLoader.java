package kasuga.lib.core.javascript;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.engine.JavascriptModuleScope;

public class ContextModuleLoader {
    private JavascriptModuleScope scope;

    public ContextModuleLoader(ContextModuleLoader parent){
        this.scope = new JavascriptModuleScope(parent.scope);
    }

    public void registerPackage(NodePackage nodePackage){
        this.scope.packages.add(nodePackage);
    }

    public void unregisterPackage(NodePackage nodePackage){
        this.scope.packages.remove(nodePackage);
    }
}
