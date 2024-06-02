package kasuga.lib.core.javascript.prebuilt.registry;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.module.ModuleLoader;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Objects;
import java.util.Optional;

public class RegistryLoader implements ModuleLoader {
    @Override
    public boolean isLoadable(String identifier) {
        return Objects.equals(identifier, "kasuga:internal/registry");
    }

    @Override
    public Optional<Value> load(Context context, String module, Value requireFn, JavascriptContext javascriptContext) {
        return Objects.equals(module, "kasuga:internal/registry") ? Optional.of(
                javascriptContext.createNativeModule(module, (e) -> new RegistryPrebuiltModule(KasugaLib.STACKS.JAVASCRIPT.registry,javascriptContext))
        ) :
        Optional.empty();
    }
}
