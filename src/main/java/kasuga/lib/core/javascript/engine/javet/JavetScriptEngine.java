package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.ScriptEngine;

public class JavetScriptEngine implements ScriptEngine {
    public JavetContext createInstance(JavascriptContext context){
        try {
            return new JavetContext(context);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }
}
