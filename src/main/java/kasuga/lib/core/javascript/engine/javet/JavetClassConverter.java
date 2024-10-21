package org.example;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.*;
import kasuga.lib.core.javascript.engine.HostAccess;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavetClassConverter {
    HashMap<Class<?>, V8ValueObject> prototypes = new HashMap<>();
    public V8ValueObject createPrototype(V8Runtime runtime, Class<?> objectClass,
                                         HashMap<String, ArrayList<Method>> methodsFromName,
                                         HashMap<Field, Boolean> properties){

        String protoName = "Kasuga#Proxy.ProtoType."+objectClass.getName();
        try{
            if(runtime.getGlobalObject().hasPrivateProperty(protoName)){
                return runtime.getGlobalObject().getPrivatePropertyObject(protoName);
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
                            V8Value _object = v8Object.getPrivateProperty("Kasuga#ProxyHandler");
                            if(_object == null || _object.isNullOrUndefined() || !(_object instanceof V8ValueObject object)){
                                throw new IllegalStateException("Illegal invocation");
                            }
                            ArrayList<V8Value> _args = new ArrayList<>();
                            _args.add(runtime.createV8ValueString(entry.getKey()));
                            if(args!= null && args.length != 0)
                                _args.addAll(List.of(args));
                            return object.invoke("invoke", (Object[])_args.toArray() );
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
                            V8Value _object = v8Object.getPrivateProperty("Kasuga#ProxyHandler");
                            if(_object == null || _object.isNullOrUndefined() || !(_object instanceof V8ValueObject object)){
                                throw new IllegalStateException("Illegal invocation");
                            }
                            return object.invoke("get", entry.getKey().getName());
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
                                V8Value _object = v8Object.getPrivateProperty("Kasuga#ProxyHandler");
                                if(_object == null || _object.isNullOrUndefined() || !(_object instanceof V8ValueObject object)){
                                    throw new IllegalStateException("Illegal invocation");
                                }
                                return object.invoke("set", entry.getKey().getName(), value);
                            }
                    );
                }
                if(setter != null){
                    prototypeObject.bindProperty(getter,setter);
                }else{
                    prototypeObject.bindProperty(getter);
                }
            }
            V8ValueSymbol symbol = ((V8ValueObject)runtime.getGlobalObject().getProperty("Symbol")).get("toStringTag");
            prototypeObject.set(symbol, objectClass.getName());
            runtime.getGlobalObject().setPrivateProperty(protoName, prototypeObject);
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
            HashMap<String, ArrayList<Method>> methodsFromName
    ) throws JavetException {
        if(methodsFromName.containsKey("apply")){
            JavetCallbackContext callbackContext = new JavetCallbackContext(
                    "apply",
                    JavetCallbackType.DirectCallNoThisAndResult,
                    (IJavetDirectCallable.NoThisAndResult) (V8Value ...args) -> InvokeHandler.invoke(object, methodsFromName.get("apply"), args)
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
            V8ValueObject prototype
    ) throws JavetException {
        V8ValueObject base = generateOperationBaseForObject(
                runtime,object,objectClass,methodsFromName
        );
        // base.set("__proto__",prototype);
        ((V8ValueFunction)
                ((V8ValueObject)runtime.getGlobalObject().getProperty("Object"))
                        .getProperty("setPrototypeOf")).callVoid(null, base, prototype);
        V8ValueObject proxyHandler = createProxyHandler(
                runtime, object, objectClass, methodsFromName
        );
        base.setPrivateProperty("Kasuga#ProxyHandler", proxyHandler);
        return base;
    }

    private <T> V8ValueObject createProxyHandler(
            V8Runtime runtime,
            T object,
            Class<T> objectClass,
            HashMap<String, ArrayList<Method>> methodsFromName
    ) throws JavetException {
        V8ValueObject proxyHandler = runtime.createV8ValueObject();
        JavetCallbackContext invokeFunction = new JavetCallbackContext(
                "invoke",
                JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult) (V8Value ...args) -> {
                    ArrayList<Method> methods = methodsFromName.get(args[0].asString());
                    List<V8Value> arguments = List.of(args);
                    V8Value[] passedArguments = arguments.subList(1, arguments.size()).toArray(new V8Value[0]);
                    return InvokeHandler.invoke(object, methods, passedArguments);
                }
        );
        proxyHandler.bindFunction(invokeFunction);
        JavetCallbackContext setFunction = new JavetCallbackContext(
                "set",
                JavetCallbackType.DirectCallNoThisAndNoResult,
                (IJavetDirectCallable.NoThisAndNoResult) (V8Value ...args) -> {
                    InvokeHandler.set(object, args[0], args[1]);
                }
        );
        proxyHandler.bindFunction(setFunction);
        JavetCallbackContext getFunction = new JavetCallbackContext(
                "get",
                JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult) (V8Value ...args) -> {
                    return InvokeHandler.get(object, args[0]);
                }
        );
        proxyHandler.bindFunction(getFunction);
        return proxyHandler;
    }

    public <T> V8Value toV8Value(V8Runtime runtime, T sourceObject){
        Class<T> objectClass = (Class<T>) sourceObject.getClass();
        List<Method> methods = getExportedAccessible(objectClass.getMethods());
        HashMap<String, ArrayList<Method>> methodsFromName = new HashMap<>();
        for (Method method : methods) {
            methodsFromName.computeIfAbsent(method.getName(), (e)->new ArrayList<>()).add(method);
        }
        HashMap<Field, Boolean> fields = getFields(objectClass);
        V8ValueObject prototype = createPrototype(runtime, sourceObject.getClass(), methodsFromName, fields);
        try {
            return generateOperationProxyForObject(
                    runtime,
                    sourceObject,
                    objectClass,
                    methodsFromName,
                    prototype
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

    public static class InvokeHandler{
        public static V8Value invoke(Object object, ArrayList<Method> alternativeMethods, V8Value ...args){
            System.out.println("Object Invoker call: " + object.getClass().getName() + "::" + alternativeMethods.get(0).getName());
            return null;
        }

        public static V8Value get(Object object, V8Value name){
            try {
                System.out.println("Object Getter call: " + object.getClass().getName() + "::" + name.asString());
            } catch (JavetException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        public static V8Value set(Object object, V8Value name, V8Value value){

            try {
                System.out.println("Object Setter call: " +object.getClass().getName() + "::" + name.asString() + "=" +value.asString());
            } catch (JavetException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
