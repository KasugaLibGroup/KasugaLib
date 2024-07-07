package kasuga.lib.core.javascript;


import kasuga.lib.core.addons.node.EntryType;
import kasuga.lib.core.addons.node.NodePackageLoader;
import kasuga.lib.core.javascript.module.node.CommonJSModuleLoader;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModuleLoader;
import kasuga.lib.core.javascript.registration.RegistrationRegistry;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class JavascriptApi {
    public final JavascriptThreadGroup GROUP_MAIN = new JavascriptThreadGroup("main");
    public JavascriptThreadGroup GROUP_CLIENT;
    public JavascriptThreadGroup GROUP_SERVER;

    public NodePackageLoader CLIENT_LOADER;
    public RegistrationRegistry registry;
    public Optional<HashMap<UUID, Object>> ASSETS = Optional.empty();

    public void setupClient(){
        GROUP_CLIENT = GROUP_MAIN.createChild("client");
        CLIENT_LOADER = new NodePackageLoader();
        CLIENT_LOADER.bindRuntime(GROUP_CLIENT, EntryType.CLIENT);
        GROUP_CLIENT.getModuleLoader().getLoader().register(new CommonJSModuleLoader());
        GROUP_CLIENT.getModuleLoader().getLoader().register(new PrebuiltModuleLoader());
        registry = new RegistrationRegistry();
        ASSETS = Optional.of(new HashMap<UUID, Object>());
    }

    public void setupServer(){

    }
}
