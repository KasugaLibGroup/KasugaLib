package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.*;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.data_type.Pair;

import java.lang.reflect.*;
import java.util.*;

public class JavetClassConverter {
    protected final JavetKasugaConverter converter;
    private final V8Runtime v8Runtime;
    private final V8ValueFunction setPrototypeOf;
    private final V8ValueObject globalObject;
    private final V8ValueSymbol toStringTag;

    JavetClassConverter(V8Runtime v8Runtime, JavetKasugaConverter converter){
        this.converter = converter;
        this.v8Runtime = v8Runtime;
        try {
            this.setPrototypeOf = ((V8ValueFunction) ((V8ValueObject)v8Runtime.getGlobalObject().getProperty("Object")).getProperty("setPrototypeOf"));
            this.globalObject = v8Runtime.getGlobalObject();
            this.toStringTag = ((V8ValueObject)v8Runtime.getGlobalObject().getProperty("Symbol")).get("toStringTag");;
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }
    public V8ValueObject createPrototype(V8Runtime runtime, Class<?> objectClass,
                                         HashMap<String, ArrayList<Method>> methodsFromName,
                                         HashMap<Field, Boolean> properties){

        String protoName = "Kasuga#Proxy.ProtoType."+objectClass.getName();
        try{
            if(globalObject.hasPrivateProperty(protoName)){
                return globalObject.getPrivateProperty(protoName);
            }
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }

        try {
            V8ValueObject prototypeObject = runtime.createV8ValueObject();
            for(Map.Entry<String,ArrayList<Method>> entry : methodsFromName.entrySet()){
                JavetCallbackContext functionContext = new JavetCallbackContext(
                        entry.getKey(),
                        JavetCallbackType.DirectCallThisAndResult,
                        (IJavetDirectCallable.ThisAndResult)(V8Value _this,V8Value ...args)->{
                            if(!(_this instanceof V8ValueObject v8Object)){
                                throw new IllegalStateException("Illegal invocation");
                            }
                            try(V8Value _object = v8Object.getPrivateProperty("Kasuga#ProxyHandler");) {
                                if (_object == null || _object.isNullOrUndefined() || !(_object instanceof V8ValueObject object)) {
                                    throw new IllegalStateException("Illegal invocation");
                                }
                                ArrayList<V8Value> _args = new ArrayList<>();
                                _args.add((runtime.createV8ValueString("invoke")));
                                _args.add((runtime.createV8ValueString(entry.getKey())));
                                if (args != null && args.length != 0)
                                    _args.addAll(List.of(args));
                                return object.invoke("operate", (Object[]) _args.toArray());
                            }
                        }
                );
                prototypeObject.bindFunction(functionContext);
            }
            for(Map.Entry<Field, Boolean> entry:properties.entrySet()){
                JavetCallbackContext getter=null,setter = null;
                getter = new JavetCallbackContext(
                        entry.getKey().getName(),
                        JavetCallbackType.DirectCallGetterAndThis,
                        (IJavetDirectCallable.GetterAndThis)(V8Value _this)->{
                            if(!(_this instanceof V8ValueObject v8Object)){
                                throw new IllegalStateException("Illegal invocation");
                            }
                            try(V8Value _object = v8Object.getPrivateProperty("Kasuga#ProxyHandler");) {
                                if (_object == null || _object.isNullOrUndefined() || !(_object instanceof V8ValueObject object)) {
                                    throw new IllegalStateException("Illegal invocation");
                                }
                                return object.invoke("operate", "get", entry.getKey().getName());
                            }
                        }
                );
                if(entry.getValue()){
                    setter = new JavetCallbackContext(
                            entry.getKey().getName(),
                            JavetCallbackType.DirectCallSetterAndThis,
                            (IJavetDirectCallable.SetterAndThis)(V8Value _this,V8Value value)->{
                                if(!(_this instanceof V8ValueObject v8Object)){
                                    throw new IllegalStateException("Illegal invocation");
                                }
                                try(V8Value _object = v8Object.getPrivateProperty("Kasuga#ProxyHandler");) {
                                    if (_object == null || _object.isNullOrUndefined() || !(_object instanceof V8ValueObject object)) {
                                        throw new IllegalStateException("Illegal invocation");
                                    }
                                    return object.invoke("operate", "set", entry.getKey().getName(), value);
                                }
                            }
                    );
                }
                if(setter != null){
                    prototypeObject.bindProperty(getter,setter);
                }else{
                    prototypeObject.bindProperty(getter);
                }
            }

            prototypeObject.set(toStringTag, objectClass.getName());
            globalObject.setPrivateProperty(protoName, prototypeObject);
            return prototypeObject;
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends AccessibleObject> List<T> getExportedAccessible(T[] sources){
        ArrayList<T> objects = new ArrayList<>();
        for(T source:sources){
            if(
                    source instanceof Member member &&
                            !Modifier.isStatic(member.getModifiers())&&
                            source.isAnnotationPresent(HostAccess.Export.class)
            ){
                objects.add(source);
            }
        }
        return objects;
    }

    public <T> V8ValueObject generateOperationBaseForObject(
            V8Runtime runtime,
            T object,
            Class<T> objectClass,
            HashMap<String, ArrayList<Method>> methodsFromName,
            InvokeHandler handler
    ) throws JavetException {
        if(methodsFromName.containsKey("apply")){
            JavetCallbackContext callbackContext = new JavetCallbackContext(
                    "apply",
                    JavetCallbackType.DirectCallNoThisAndResult,
                    (IJavetDirectCallable.NoThisAndResult) (V8Value ...args) -> handler.invoke(object, methodsFromName.get("apply"), args)
            );
            return runtime.createV8ValueFunction(callbackContext);
        }else{
            return runtime.createV8ValueObject();
        }
    }

    public <T> V8Value generateOperationProxyForObject(
            V8Runtime runtime,
            T object,
            Class<T> objectClass,
            HashMap<String, ArrayList<Method>> methodsFromName,
            V8ValueObject prototype,
            HashMap<Field, Boolean> fields) throws JavetException {
        InvokeHandler invokeHandler = new InvokeHandler(object, methodsFromName, fields);
        V8ValueObject base = generateOperationBaseForObject(
                runtime,object,objectClass,methodsFromName,invokeHandler
        );
        // base.set("__proto__",prototype);
        setPrototypeOf.callVoid(null, base, prototype);
        prototype.close();
        V8ValueObject proxyHandler = createProxyHandler(
                runtime, object, objectClass, methodsFromName,fields, invokeHandler
        );
        base.setPrivateProperty("Kasuga#ProxyHandler", proxyHandler);
        proxyHandler.close();
        base.setWeak();
        return base;
    }

    private <T> V8ValueObject createProxyHandler(
            V8Runtime runtime,
            T object,
            Class<T> objectClass,
            HashMap<String, ArrayList<Method>> methodsFromName,
            HashMap<Field, Boolean> fields,
            InvokeHandler invokeHandler) throws JavetException {
        V8ValueObject proxyHandler = runtime.createV8ValueObject();
        JavetCallbackContext operateFunction = new JavetCallbackContext(
                "operate",
                InvokeHandler.createFunction(invokeHandler, (Object) object),
                InvokeHandler.FunctionHandlerExecute
        );
        try(V8ValueFunction v8Function = v8Runtime.createV8ValueFunction(operateFunction);){
            proxyHandler.set("operate", v8Function);
        }
        proxyHandler.bindFunction(operateFunction);
        return proxyHandler;
    }

    public <T> V8Value toV8Value(V8Runtime runtime, T sourceObject){
        Class<T> objectClass = (Class<T>) sourceObject.getClass();
        List<Method> methods = getExportedAccessible(objectClass.getMethods());
        HashMap<String, ArrayList<Method>> methodsFromName = new HashMap<>();
        for (Method method : methods) {
            methodsFromName.computeIfAbsent(method.getName(), (e)->new ArrayList<>()).add(method);
        }
        if(
                objectClass.isAnnotationPresent(FunctionalInterface.class) ||
                        Arrays.stream(objectClass.getInterfaces()).anyMatch((t)->t.isAnnotationPresent(FunctionalInterface.class))
        ){
            Method method = objectClass.getDeclaredMethods()[0];
            if(
                    !methodsFromName.containsKey(method.getName()) ||
                            !methodsFromName.get(method.getName()).contains(method)
            ){
                methodsFromName.computeIfAbsent(method.getName(), (e)->new ArrayList<>()).add(method);
            }
        }
        HashMap<Field, Boolean> fields = getFields(objectClass);
        V8ValueObject prototype = createPrototype(runtime, sourceObject.getClass(), methodsFromName, fields);
        try {
            return generateOperationProxyForObject(
                    runtime,
                    sourceObject,
                    objectClass,
                    methodsFromName,
                    prototype,
                    fields
            );
        } catch (JavetException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> HashMap<Field, Boolean> getFields(Class<T> objectClass) {
        Field[] fields = objectClass.getFields();
        HashMap<Field, Boolean> returnFields = new HashMap<>();
        for(Field field:fields){
            if(
                    !Modifier.isStatic(field.getModifiers()) &&
                            field.isAnnotationPresent(HostAccess.Export.class)
            )
                returnFields.put(field, !Modifier.isFinal(field.getModifiers()));
        }
        return returnFields;
    }

    protected class InvokeHandler{
        private final Object object;
        private final HashMap<String, ArrayList<Method>> methodsFromName;
        private final HashMap<Field, Boolean> fields;

        private final HashMap<String, ArrayList<Field>> fieldsByName = new HashMap<>();

        public <T> InvokeHandler(T object, HashMap<String, ArrayList<Method>> methodsFromName, HashMap<Field, Boolean> fields) {
            this.object = object;
            this.methodsFromName = methodsFromName;
            this.fields = fields;
            for (Field field : this.fields.keySet()) {
                fieldsByName.computeIfAbsent(field.getName(), (n)->new ArrayList<>()).add(field);
            }
        }

        public V8Value invoke(Object object, ArrayList<Method> alternativeMethods, V8Value ...args){
            List<V8Value> argsList = List.of(args);
            ArrayList<Object> converted = new ArrayList<>();
            try{
                for (V8Value value : argsList) {
                    converted.add(converter.toObject(value));
                }
                for (Method sameNameMethod : alternativeMethods) {
                    if(!sameNameMethod.canAccess(object))
                        continue;
                    if(sameNameMethod.getParameterCount() != argsList.size())
                        continue;
                    Pair<Boolean, V8Value> callResult = tryInvoke(sameNameMethod, argsList, converted);
                    if(callResult.getFirst()){
                        if(callResult.getSecond() instanceof V8ValueReference reference){
                            reference.setWeak();
                        }
                        return callResult.getSecond();
                    }
                }
            }catch (JavetException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            throw new RuntimeException("Invalid call target to "+object.getClass().getName()+"::"+alternativeMethods.get(0).getName());
        }

        public Pair<Boolean, V8Value> tryInvoke(Method method, List<V8Value> originValues, List<Object> convertedValues) throws JavetException, InvocationTargetException, IllegalAccessException {
            Parameter[] parameters = method.getParameters();
            List<Object> callParameter = new ArrayList<>();
            int i=0;
            for(Parameter parameter : parameters){
                if(parameter.getType() == JavascriptValue.class){
                    V8Value value = JavetValue.weakClone(originValues.get(i));
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
            return Pair.of(true,converter.toV8Value(v8Runtime,returnValue));
        }

        public V8Value get(Object object, V8Value name){
            try {
                ArrayList<Field> fieldArrayList = fieldsByName.get(name.asString());
                if(fieldArrayList == null)
                    return null;
                for (Field field : fieldArrayList) {
                    if(field.canAccess(object)){
                        return converter.toV8Value(v8Runtime,field.get(object));
                    }
                }
                return null;
            } catch (JavetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public V8Value set(Object object, V8Value name, V8Value value){

            try {
                System.out.println("Object Setter call: " +object.getClass().getName() + "::" + name.asString() + "=" +value.asString());
            } catch (JavetException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        public <T> V8Value invokeExternal(T object, V8Value... args) {
            try{
                switch (args[0].asString()){
                    case "invoke":
                        ArrayList<Method> methods = methodsFromName.get(args[1].asString());
                        List<V8Value> arguments = List.of(args);
                        V8Value[] passedArguments = arguments.subList(2, arguments.size()).toArray(new V8Value[0]);
                        return invoke(object, methods, passedArguments);
                    case "set":
                        return set(object,args[1],args[2]);
                    case "get":
                        return get(object, args[1]);
                }
            } catch (JavetException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        public static interface FunctionHandler{
            public V8Value execute(V8Value ...args);
        }

        public static FunctionHandler createFunction(InvokeHandler invokeHandler, Object object){
            return (V8Value ...args)->invokeHandler.invokeExternal(object, args);
        }

        public static Method FunctionHandlerExecute;
        static {
            FunctionHandlerExecute = (FunctionHandler.class).getMethods()[0];
        }
    }
}
