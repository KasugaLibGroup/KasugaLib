package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.annotations.V8Convert;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetEntityFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.utils.receivers.JavetCallbackReceiver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueReference;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import kasuga.lib.core.javascript.engine.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.data_type.Pair;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/*
 THIS FILE WAS MODIFIED FROM https://www.caoccao.com/Javet/reference/converters/custom_converter.html
 */
public class JavetKasugaConverter extends JavetObjectConverter {

    JavetClassConverter classConverter;
    private final V8Runtime runtime;

    private final V8ValueSymbol SYMBOL_NATIVE_OBJECT;
    HashMap<Integer, WeakReference<Object>> cachedObjects = new HashMap<>();

    JavetKasugaConverter(V8Runtime runtime){
        this.runtime = runtime;
        try{
            SYMBOL_NATIVE_OBJECT = runtime.createV8ValueSymbol("NATIVE OBJECT");
            classConverter = new JavetClassConverter(runtime,this);
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object, int depth) throws JavetException {
        if(object == null){
            return (T) v8Runtime.createV8ValueNull();
        }
        if(object instanceof JavetJavascriptValue value){
            return (T) value.getValue().toClone();
        }
        T v8Value = super.toV8Value(v8Runtime, object, depth);

        if (v8Value != null && !(v8Value.isUndefined())) {
            if(v8Value instanceof V8ValueObject v8ValueObject){
                int hashCode = System.identityHashCode(object);
                v8ValueObject.setInteger(SYMBOL_NATIVE_OBJECT, hashCode);
                cachedObjects.put(hashCode, new WeakReference<>(object));
            }
            return v8Value;
        }

        V8Value v8ValueConverted = classConverter.toV8Value(runtime, object);

        if (v8ValueConverted != null && !(v8ValueConverted.isUndefined())) {
            if(v8ValueConverted instanceof V8ValueObject v8ValueObject){
                int hashCode = System.identityHashCode(object);
                v8ValueObject.setProperty(SYMBOL_NATIVE_OBJECT,hashCode);
                cachedObjects.put(hashCode, new WeakReference<>(object));
            }

            return (T) v8ValueConverted;
        }
        throw new IllegalStateException("Unknown converting");
    }


    @Override
    protected <T> T toObject(V8Value v8Value, int depth) throws JavetException {
        if(v8Value instanceof V8ValueObject object && object.has(SYMBOL_NATIVE_OBJECT)){
            int address = object.getInteger(SYMBOL_NATIVE_OBJECT);
            if(cachedObjects.containsKey(address)){
                Object nativeObject = cachedObjects.get(address).get();
                return (T) nativeObject;
            }
        }
        T parentConvertResult = super.toObject(v8Value, depth);
        if(parentConvertResult instanceof V8Value){
            return (T) new JavetJavascriptValue(JavetValue.weakClone(v8Value), v8Value.getV8Runtime());
        }
        return parentConvertResult;
    }
}