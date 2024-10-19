package kasuga.lib.core.javascript;


import kasuga.lib.core.addons.node.EntryType;
import kasuga.lib.core.addons.node.NodePackageLoader;
import kasuga.lib.core.javascript.engine.ScriptEngine;
import kasuga.lib.core.javascript.engine.ScriptEngines;
import kasuga.lib.core.javascript.module.NodeModuleResolver;
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
    public NodePackageLoader SERVER_LOADER;
    public RegistrationRegistry registry;
    public Optional<HashMap<UUID, Object>> ASSETS = Optional.empty();

    public void setupClient(){
        GROUP_CLIENT = GROUP_MAIN.createChild("client");
        GROUP_CLIENT.setScriptEngine(ScriptEngines.JAVET.get());
        CLIENT_LOADER = new NodePackageLoader();
        CLIENT_LOADER.bindRuntime(GROUP_CLIENT, EntryType.CLIENT);
        GROUP_CLIENT.getModuleLoader().getLoader().register(new NodeModuleResolver());
        GROUP_CLIENT.getModuleLoader().getLoader().register(new PrebuiltModuleLoader());

        registry = new RegistrationRegistry();
        ASSETS = Optional.of(new HashMap<UUID, Object>());
    }

    public void setupServer(){
        GROUP_SERVER = GROUP_MAIN.createChild("server");
        GROUP_SERVER.setScriptEngine(ScriptEngines.JAVET.get());
        SERVER_LOADER = new NodePackageLoader();
        SERVER_LOADER.bindRuntime(GROUP_SERVER, EntryType.SERVER);
        GROUP_SERVER.getModuleLoader().getLoader().register(new NodeModuleResolver());
        GROUP_SERVER.getModuleLoader().getLoader().register(new PrebuiltModuleLoader());
    }

    public void destoryServer(){
        GROUP_SERVER.terminate();
    }
}
