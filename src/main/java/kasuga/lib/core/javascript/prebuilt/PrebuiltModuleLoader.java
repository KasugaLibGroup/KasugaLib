package kasuga.lib.core.javascript.prebuilt;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.ModuleLoader;
import kasuga.lib.core.javascript.prebuilt.process.ProcessModule;
import kasuga.lib.core.javascript.prebuilt.timer.TimerPrebuiltModule;
import kasuga.lib.core.javascript.prebuilt.websocket.WebSocketPrebuiltModule;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Optional;

public class PrebuiltModuleLoader implements ModuleLoader {

    static final String PREFIX = "kasuga_lib:native/";

    @Override
    public boolean isLoadable(String identifier) {
        return identifier.startsWith(PREFIX);
    }

    @Override
    public Optional<Value> load(Context context, String module, Value requireFn, JavascriptContext javascriptContext) {
        String realIdentifier = module.substring(PREFIX.length());
        System.out.printf("Loading kasuga native module %s ( %s )%n",realIdentifier,module);
        return switch (realIdentifier) {
            case "timer" -> Optional.of(
                    javascriptContext.createNativeModule(module, (e) -> new TimerPrebuiltModule(javascriptContext))
            );
            case "websocket" -> Optional.of(
                    javascriptContext.createNativeModule(module, (e) -> new WebSocketPrebuiltModule(javascriptContext))
            );
            case "process" -> Optional.of(
                    javascriptContext.createNativeModule(module, (e) -> new ProcessModule(javascriptContext))
            );
            default -> Optional.empty();
        };
    }
}
