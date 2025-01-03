package kasuga.lib.core.javascript.engine.javet.converter;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.converters.IJavetConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.javascript.engine.javet.JavetJavascriptValue;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;

public class ClassAccessor {
    private final V8Runtime runtime;
    private final IJavetConverter provider;
    private final Class<?> classType;
    Map<String, Function<V8Value[], V8Value>> quickAccessors = new HashMap<>();
    Map<String, MethodOverrideMap> methods = new HashMap<>();
    Map<String, Field> fields = new HashMap<>();

    public ClassAccessor(
            V8Runtime runtime,
            IJavetConverter provider,
            Class<?> classType
    ){
        this.runtime = runtime;
        this.provider = provider;
        this.classType = classType;
    }


    public Object invoke(
            Object target,
            String name,
            V8Value ...value
    ) throws InvocationTargetException, IllegalAccessException, JavetException {
        System.gc();
        runtime.lowMemoryNotification();
        if(quickAccessors.containsKey(name)) {
            return quickAccessors.get(name).apply(value);
        }

        if(!methods.containsKey(name)) {
            throw new RuntimeException("Illegal invocation");
        }

        MethodOverrideMap overrideMap = methods.get(name);

        int valueSize = value == null ? 0 : value.length;
        BitSet convertMask = overrideMap.converterMask.get(valueSize);
        List<Method> localMethods = overrideMap.methods.get(valueSize);

        if(convertMask == null || localMethods == null){
            throw new RuntimeException("Illegal invocation");
        }

        Object[] arrayParameters = new Object[valueSize];

        for(int i = 0; i < valueSize ; i++) {
            if(!convertMask.get(i)){
                arrayParameters[i] = value[i];
                continue;
            }
            Object nativeObject = provider.toObject(value[i]);
            arrayParameters[i] = nativeObject == null ?
                    value[i] : nativeObject;
        }

        JavascriptValue[] values = new JavascriptValue[valueSize];

        for(int methodIndex = 0; methodIndex < localMethods.size(); methodIndex++){
            Method localMethod = localMethods.get(methodIndex);
            Class<?>[] parameterTypes = localMethod.getParameterTypes();
            Object[] parameters = new Object[valueSize];
            boolean isSignatureMatch = true;
            for (int parameterIndex = parameterTypes.length - 1; parameterIndex >= 0; parameterIndex--) {
                Class<?> parameterType = parameterTypes[parameterIndex];
                if(parameterType == JavascriptValue.class){
                    if(values[parameterIndex] == null){
                        values[parameterIndex] = new JavetJavascriptValue(value[parameterIndex], runtime);
                    }
                    parameters[parameterIndex] = value[parameterIndex];
                    continue;
                }

                Class<?> valueType = arrayParameters[parameterIndex].getClass();
                if(
                        parameterType == valueType ||
                        parameterType.isAssignableFrom(valueType)
                ) {
                    parameters[parameterIndex] = arrayParameters[parameterIndex];
                    continue;
                }
                isSignatureMatch = false;
                break;
            }

            if(!isSignatureMatch){
                continue;
            }

            return localMethod.invoke(target, parameters);
        }
        throw new RuntimeException("Illegal invocation");
    }

    public Object get(Object object, String name) throws IllegalAccessException {
        return fields.get(name).get(object);
    }

    public Object set(
            Object object,
            String name,
            V8Value value
    ) throws JavetException, IllegalAccessException {
        Object nativeObject = provider.toObject(value);
        if(
                !fields.get(name).getType().isAssignableFrom(nativeObject.getClass()) &&
                !Modifier.isFinal(fields.get(name).getModifiers())
        ) {
            throw new RuntimeException("Ileegal operation");
        }
        fields.get(name).set(object, nativeObject);
        return nativeObject;
    }

    public void addMethod(String name, Method method){
        MethodOverrideMap overrideMap = methods.computeIfAbsent(name, (i)->new MethodOverrideMap());
        overrideMap.initIfAbsent(method.getParameterCount());
        overrideMap.methods.get(method.getParameterCount()).add(method);
        Class<?>[] parameterTypes = method.getParameterTypes();

        for (int i = 0; i < parameterTypes.length; i++) {
            if(parameterTypes[i] == JavascriptValue.class){
                overrideMap.converterMask.get(method.getParameterCount()).set(i, false);
            }
        }

        if(method.getReturnType() != Void.class){
            overrideMap.isVoidReturn = false;
        }

    }

    public void addField(String name, Field field){
        this.fields.put(name, field);
    }

    public static ClassAccessor collect(
            V8Runtime runtime,
            IJavetConverter converter,
            Class<?> classType
    ){
        ClassAccessor accessor = new ClassAccessor(runtime, converter, classType);
        Method[] methods = classType.getMethods();
        HashSet<Method> filteredMethods = new HashSet<>();
        for (Method method : methods) {
            if(method.isAnnotationPresent(HostAccess.Export.class)){
                filteredMethods.add(method);
            }
        }
        Class<?>[] interfaces = classType.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            if(interfaces[i].isAnnotationPresent(FunctionalInterface.class)){
                Method[] interfaceMethods = interfaces[i].getDeclaredMethods();
                for (Method interfaceMethod : interfaceMethods) {
                    for(Method method : methods){
                        if(
                                Arrays.equals(method.getTypeParameters(), interfaceMethod.getTypeParameters()) &&
                                        method.getReturnType() == interfaceMethod.getReturnType()
                        ){
                            filteredMethods.add(method);
                        }
                    }
                }
            }
        }

        for (Method filteredMethod : filteredMethods) {
            filteredMethod.setAccessible(true);
            accessor.addMethod(filteredMethod.getName(), filteredMethod);
        }

        Field[] fields = classType.getFields();

        for (Field field : fields) {
            if(field.isAnnotationPresent(HostAccess.Export.class)){
                accessor.addField(field.getName(), field);
            }
        }

        return accessor;
    }

    public void bindPrototypeTo(V8Runtime runtime, FastJavetClassConverter converter, V8ValueObject value) throws JavetException {
        for (Map.Entry<String, MethodOverrideMap> entry : methods.entrySet()) {
            String name = entry.getKey();
            MethodOverrideMap overrideMap = entry.getValue();
            bind(runtime, value, converter, name, overrideMap.isVoidReturn);
        }

        for (Map.Entry<String, Field> fieldEntry : fields.entrySet()){
            String name = fieldEntry.getKey();
            Field field = fieldEntry.getValue();
            bindProp(runtime, value, converter, name, Modifier.isFinal(field.getModifiers()));
        }
    }

    protected void bind(
            V8Runtime runtime,
            V8ValueObject value,
            FastJavetClassConverter converter,
            String name,
            boolean voidReturn
    ) throws JavetException {
        System.out.printf("BIND RUNTIME: %s \n", name);
        JavetCallbackContext context = NativeProxyAccessor.getCallbackContext(
                runtime,
                converter,
                this,
                classType,
                name,
                voidReturn ?
                        NativeProxyAccessor.AccessorType.METHOD_VOID
                        : NativeProxyAccessor.AccessorType.METHOD
                );
        value.bindFunction(context);
    }


    protected void bindProp(
            V8Runtime runtime,
            V8ValueObject value,
            FastJavetClassConverter converter,
            String name,
            boolean isReadOnly
    ) throws JavetException {
        System.out.printf("BIND RUNTIME: %s \n", name);
        JavetCallbackContext readContext = NativeProxyAccessor.getCallbackContext(
                runtime,
                converter,
                this,
                classType,
                name,
                NativeProxyAccessor.AccessorType.FIELD_READ
        );
        if(isReadOnly){
            value.bindProperty(readContext);
            return;
        }
        JavetCallbackContext writeContext = NativeProxyAccessor.getCallbackContext(
                runtime,
                converter,
                this,
                classType,
                name,
                NativeProxyAccessor.AccessorType.FIELD_WRITE
        );
        value.bindProperty(readContext, writeContext);
    }
}
