package kasuga.lib.core.client.frontend.commands;

import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.engine.AbstractJavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;

public class MetroLoaderModule extends AbstractJavascriptEngineModule {
    private MetroModuleInfo metroInfo;
    MetroLoaderModule(MetroModuleInfo info){
        metroInfo = info;
    }

    @Override
    public Object getFeature(String name) {
        if(name == "metro"){
            return metroInfo;
        }
        return super.getFeature(name);
    }

    @Override
    public String getAbsoultePath() {
        return "";
    }

    @Override
    public NodePackage getPackage() {
        return null;
    }

    @Override
    public String getDirectoryName() {
        return "";
    }
}
