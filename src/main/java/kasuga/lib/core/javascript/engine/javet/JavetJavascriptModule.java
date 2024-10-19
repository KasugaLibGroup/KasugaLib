package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.values.reference.IV8Module;
import kasuga.lib.core.addons.node.NodePackage;
import kasuga.lib.core.javascript.engine.AbstractJavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;

public class JavetJavascriptModule extends AbstractJavascriptEngineModule {
    IV8Module module;
    String absolutePath;
    NodePackage nodePackage;
    String directoryName;
    public JavetJavascriptModule(
            IV8Module module,
            NodePackage nodePackage,
            String absolutePath,
            String directoryName
    ){
        this.module = module;
        this.absolutePath = absolutePath;
        this.nodePackage = nodePackage;
        this.directoryName = directoryName;
    }
    public IV8Module getModule() {
        return module;
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
