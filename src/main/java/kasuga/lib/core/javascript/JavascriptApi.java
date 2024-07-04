package kasuga.lib.core.javascript;


import kasuga.lib.core.addons.node.EntryType;
import kasuga.lib.core.addons.node.NodePackageLoader;
import kasuga.lib.core.javascript.module.node.CommonJSModuleLoader;
import kasuga.lib.core.javascript.registration.RegistrationRegistry;

public class JavascriptApi {
    public final JavascriptThreadGroup GROUP_MAIN = new JavascriptThreadGroup("main");
    public JavascriptThreadGroup GROUP_CLIENT;
    public JavascriptThreadGroup GROUP_SERVER;

    public NodePackageLoader CLIENT_LOADER;
    public RegistrationRegistry registry;

    public void setupClient(){
        GROUP_CLIENT = GROUP_MAIN.createChild("client");
        CLIENT_LOADER = new NodePackageLoader();
        CLIENT_LOADER.bindRuntime(GROUP_CLIENT, EntryType.CLIENT);
        GROUP_CLIENT.getModuleLoader().getLoader().register(new CommonJSModuleLoader());
    }

    public void setupServer(){

    }
}
