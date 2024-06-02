package kasuga.lib.core.javascript.module;

import kasuga.lib.core.javascript.JavascriptContext;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.util.Optional;

public interface ModuleLoader {
    public boolean isLoadable(String identifier);

    public Optional<Value> load(Context context, String module, Value requireFn, JavascriptContext javascriptContext);
}
