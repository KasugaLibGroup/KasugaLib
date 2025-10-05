package kasuga.lib.core.javascript.engine.javet;

import kasuga.lib.core.javascript.engine.ScriptEngine;
import kasuga.lib.core.javascript.engine.ScriptEngineLoader;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

public class JavetScriptEngineLoader implements ScriptEngineLoader {
    @Override
    public Supplier<ScriptEngine> getSupplier() {
        return JavetScriptEngine::new;
    }

    @Override
    public void startup() {

    }
}
