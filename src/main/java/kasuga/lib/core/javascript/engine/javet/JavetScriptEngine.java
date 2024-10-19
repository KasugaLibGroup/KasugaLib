package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.interop.V8Runtime;
import kasuga.lib.core.javascript.engine.ScriptEngine;

public class JavetScriptEngine implements ScriptEngine {
    public JavetContext createInstance(){
        return new JavetContext();
    }
}
