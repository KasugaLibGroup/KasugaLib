package kasuga.lib.core.javascript.prebuilt;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineModule;
import kasuga.lib.core.javascript.engine.JavascriptModuleLoader;
import kasuga.lib.core.javascript.prebuilt.nbt.NBTModule;
import kasuga.lib.core.javascript.prebuilt.process.ProcessModule;
import kasuga.lib.core.javascript.prebuilt.registry.RegistryPrebuiltModule;
import kasuga.lib.core.javascript.prebuilt.timer.TimerPrebuiltModule;
import kasuga.lib.core.javascript.prebuilt.websocket.WebSocketPrebuiltModule;

import java.util.Optional;

public class PrebuiltModuleLoader implements JavascriptModuleLoader {
    public Optional<Object> getModule(JavascriptContext source, String name) {
        switch (name){
            case "kasuga:timer":
                return Optional.of(new TimerPrebuiltModule(source));
            case "kasuga:websocket":
                return Optional.of(new WebSocketPrebuiltModule(source));
            case "kasuga:process":
                return Optional.of(new ProcessModule(source));
            case "kasuga:registry":
                return Optional.of(new RegistryPrebuiltModule(source));
            case "kasuga:nbt":
                return Optional.of(NBTModule.getInstance());
        }

        return Optional.empty();
    }

    @Override
    public JavascriptEngineModule load(JavascriptEngineContext engineContext, String name, JavascriptEngineModule source) {
        Optional<Object> obj = getModule(engineContext.getContext(),name);
        if(obj.isPresent()){
            return engineContext.compileNativeModule(obj.get(),name);
        }
        return null;
    }
}
