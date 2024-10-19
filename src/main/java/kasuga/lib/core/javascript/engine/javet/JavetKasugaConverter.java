package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.utils.receivers.JavetCallbackReceiver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.data_type.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/*
 THIS FILE WAS MODIFIED FROM https://www.caoccao.com/Javet/reference/converters/custom_converter.html
 */
public class JavetKasugaConverter extends JavetObjectConverter {
    @Override
    protected <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object, int depth) throws JavetException {
        if(object instanceof JavetJavascriptValue value){
            return (T) value.getValue().toClone();
        }
        T v8Value = super.toV8Value(v8Runtime, object, depth);
        Class objectClass = object.getClass();
        if (v8Value != null && !(v8Value.isUndefined())) {
            return v8Value;
        }

        V8ValueObject v8ValueObject = null;

        if(objectClass.isAnnotationPresent(FunctionalInterface.class) || objectClass.getDeclaredMethods().length == 1){
            JavetCallbackContext functionContext = new JavetCallbackContext(
                    objectClass.getDeclaredMethods()[0].getName(),
                    createProxiedCallFromFunctionialInterface(object, v8Runtime),
                    PROXIED_CALL_METHOD
            );
            v8ValueObject = v8Runtime.createV8ValueFunction(functionContext);
        }else v8ValueObject = v8Runtime.createV8ValueObject();

        AtomicReference<V8ValueFunction> functionCallObject = new AtomicReference<>();

        Method[] methods = objectClass.getMethods();
        HashMap<String, ArrayList<Method>> methodsFromName = new HashMap<>();

        for (Method method : methods) {
            if (method.canAccess(object)) {
                methodsFromName.computeIfAbsent(method.getName(), (e)->new ArrayList<>()).add(method);
            }
        }

        V8ValueObject finalV8ValueObject = v8ValueObject;
        methodsFromName.forEach((methodName, sameNameMethods)->{
            if (methodName != null) {
                JavetCallbackContext functionContext = new JavetCallbackContext(
                        methodName,
                        createProxiedCallByMethods(object, sameNameMethods, v8Runtime),
                        PROXIED_CALL_METHOD
                );

                V8ValueFunction v8Function = null;
                try {
                    v8Function = v8Runtime.createV8ValueFunction(functionContext);
                    finalV8ValueObject.set(methodName, v8Function);
                } catch (JavetException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return functionCallObject.get() == null ? (T) v8ValueObject : (T) functionCallObject.get();
    }


    @Override
    protected <T> T toObject(V8Value v8Value, int depth) throws JavetException {
        T parentConvertResult = super.toObject(v8Value, depth);
        if(parentConvertResult instanceof V8Value){
            return (T) new JavetJavascriptValue(v8Value.toClone(), v8Value.getV8Runtime());
        }
        return parentConvertResult;
    }

    public static Method PROXIED_CALL_METHOD = (ProxiedCall.class).getMethods()[0];

    @FunctionalInterface
    static interface ProxiedCall{
        public V8Value call(V8Value ...args);
    }

    protected ProxiedCall createProxiedCallByMethods(Object object, List<Method> sameNameMethods, V8Runtime v8Runtime){
        ProxiedCall proxiedCall = new ProxiedCall(){
            @Override
            public V8Value call(V8Value... args) {
                List<V8Value> argsList = List.of(args);
                ArrayList<Object> converted = new ArrayList<>();
                try{
                    for (V8Value value : argsList) {
                        converted.add(toObject(value));
                    }
                    for (Method sameNameMethod : sameNameMethods) {
                        Pair<Boolean, V8Value> callResult = call(sameNameMethod, argsList, converted);
                        if(callResult.getFirst()){
                            return callResult.getSecond();
                        }
                    }
                }catch (JavetException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                throw new RuntimeException("Invalid Call Target");
            }

            protected Pair<Boolean,V8Value> call(Method method, List<V8Value> originValues, List<Object> convertedValues) throws JavetException, InvocationTargetException, IllegalAccessException {
                Parameter[] parameters = method.getParameters();
                List<Object> callParameter = new ArrayList<>();
                int i=0;
                for(Parameter parameter : parameters){
                    if(parameter.getType() == JavascriptValue.class){
                        V8Value value = originValues.get(i);
                        callParameter.add(new JavetJavascriptValue(value, v8Runtime));
                    }else{
                        if(!parameter.getType().isAssignableFrom(convertedValues.get(i).getClass())){
                            return Pair.of(false, null);
                        }
                        callParameter.add(convertedValues.get(i));
                    }
                    i++;
                }
                Object returnValue = method.invoke(object, (Object[]) callParameter.toArray());
                return Pair.of(true,toV8Value(v8Runtime,returnValue,0));
            }
        };
        return proxiedCall;
    }

    protected ProxiedCall createProxiedCallFromFunctionialInterface(Object object, V8Runtime v8Runtime){
        return createProxiedCallByMethods(object,List.of(object.getClass().getDeclaredMethods()),v8Runtime);
    }

}
