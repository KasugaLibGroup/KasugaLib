package kasuga.lib.core.javascript.engine;

import kasuga.lib.core.javascript.engine.javet.JavetScriptEngine;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

public class ScriptEngines {
    public static Lazy<JavetScriptEngine> JAVET = Lazy.of(()->new JavetScriptEngine());
}
