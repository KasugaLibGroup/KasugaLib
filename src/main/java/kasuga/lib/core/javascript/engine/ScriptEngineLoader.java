package kasuga.lib.core.javascript.engine;

import java.util.function.Supplier;

public interface ScriptEngineLoader {
    public Supplier<ScriptEngine> getSupplier();
    public void startup();
}
