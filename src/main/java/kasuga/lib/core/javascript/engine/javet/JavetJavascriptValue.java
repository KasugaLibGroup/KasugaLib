package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueNumber;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import kasuga.lib.core.javascript.engine.JavascriptValue;

public class JavetJavascriptValue implements JavascriptValue {

    private final V8Runtime runtime;
    public V8Value value;
    public V8Value reciever;

    public JavetJavascriptValue(V8Value v8Value, V8Runtime runtime) throws JavetException {
        this(v8Value, runtime.createV8ValueObject(), runtime);
    }

    public JavetJavascriptValue(V8Value v8Value, V8Value reciever, V8Runtime runtime) {
        this.value = v8Value;
        this.runtime = runtime;
    }


    @Override
    public boolean isString() {
        return value instanceof V8ValueString;
    }

    @Override
    public String asString() {
        try {
            return value.asString();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T as(Class<T> className) {
        try {
            return runtime.getConverter().toObject(value);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canExecute() {
        return value instanceof V8ValueFunction;
    }

    @Override
    public JavascriptValue execute(Object... objects) {
        try {
            V8Value _reciever = reciever;
            if(_reciever == null){

            }
            return new JavetJavascriptValue(((V8ValueFunction)value).call(reciever, objects), runtime);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void executeVoid(Object... objects) {
        try {
            ((V8ValueFunction)value).callVoid(reciever, objects);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pin() {}

    @Override
    public boolean hasMember(String memberName) {
        try {
            return ((V8ValueObject)value).has(memberName);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JavascriptValue getMember(String memberName) {
        try {
            return new JavetJavascriptValue(
                    ((V8ValueObject)value).get(memberName),
                    value,
                    runtime
            );
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JavascriptValue invokeMember(String memberName, Object... objects) {
        return this.getMember(memberName).execute(objects);
    }

    @Override
    public boolean isNumber() {
        return this.value instanceof V8ValueNumber<?>;
    }

    @Override
    public int asInt() {
        try {
            return this.value.asInt();
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public V8Value getValue(){
        return value;
    }
}
