package kasuga.lib.core.javascript.engine.javet.converter;

import com.caoccao.javet.values.V8Value;

public interface INativeClassProvider {
    public Object getNativeObject(V8Value value);
}
