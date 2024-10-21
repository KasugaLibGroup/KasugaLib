package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueReference;

public class JavetValue {
    static <T extends V8Value> T weakClone(T value) throws JavetException {
        T value1 = value.toClone();
        if(value1 instanceof V8ValueReference reference){
            reference.setWeak();
        }
        return value1;
    }
}
