package kasuga.lib.core.javascript.prebuilt;

import kasuga.lib.core.javascript.module.CachedModuleLoader;
import kasuga.lib.core.javascript.module.JavascriptModule;
import kasuga.lib.core.javascript.module.ModuleLoader;
import kasuga.lib.core.javascript.module.node.JavascriptNodeModule;
import kasuga.lib.core.javascript.prebuilt.process.ProcessModule;
import kasuga.lib.core.javascript.prebuilt.registry.RegistryPrebuiltModule;
import kasuga.lib.core.javascript.prebuilt.timer.TimerPrebuiltModule;
import kasuga.lib.core.javascript.prebuilt.websocket.WebSocketPrebuiltModule;

import java.util.Optional;

public class PrebuiltModuleLoader extends CachedModuleLoader implements ModuleLoader {

    @Override
    public Optional<JavascriptModule> getModule(JavascriptModule source, String name) {
        switch (name){
            case "kasuga:timer":
                return Optional.of(new TimerPrebuiltModule(source.getContext()));
            case "kasuga:websocket":
                return Optional.of(new WebSocketPrebuiltModule(source.getContext()));
            case "kasuga:process":
                return Optional.of(new ProcessModule(source.getContext()));
            case "kasuga:registry":
                return Optional.of(new RegistryPrebuiltModule(source.getContext()));
        }

        return Optional.empty();
    }
}
