package kasuga.lib.core.javascript.engine.javet.converter;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;

public class NativeProxyAccessor {

    public static enum AccessorType {
        FIELD_READ,
        FIELD_WRITE,
        METHOD,
        METHOD_VOID
    }
    public static JavetCallbackContext getCallbackContext(
            V8Runtime runtime,
            FastJavetClassConverter converter,
            ClassAccessor accessor,
            Class<?> className,
            String name,
            AccessorType type
    ){
        JavetCallbackContext context;
        switch (type) {
            case METHOD:
                context = new JavetCallbackContext(
                        name,
                        JavetCallbackType.DirectCallThisAndResult,
                        (IJavetDirectCallable.ThisAndResult)(V8Value _this, V8Value ...args)-> {
                            if(!(_this instanceof V8ValueObject object)){
                                throw new RuntimeException("Illegal invocation");
                            }

                            Object targetObject = converter.getNativeObject(object);

                            if(targetObject == null){
                                throw new RuntimeException("Illegal invocation");
                            }

                            if(!className.isAssignableFrom(targetObject.getClass())) {
                                throw new RuntimeException("Illegal invocation: "+className.getName() + "/" + targetObject.getClass().getName());
                            }

                            return converter.toV8Value(runtime ,accessor.invoke(targetObject, name, args));
                        }
                );
                break;

            case METHOD_VOID:
                context = new JavetCallbackContext(
                        name,
                        JavetCallbackType.DirectCallThisAndNoResult,
                        (IJavetDirectCallable.ThisAndNoResult)(V8Value _this, V8Value ...args)-> {
                            if(!(_this instanceof V8ValueObject object)){
                                throw new RuntimeException("Illegal invocation");
                            }

                            Object targetObject = converter.getNativeObject(object);

                            if(targetObject == null){
                                throw new RuntimeException("Illegal invocation");
                            }

                            if(!className.isAssignableFrom(targetObject.getClass())) {
                                throw new RuntimeException("Illegal invocation");
                            }

                            accessor.invoke(targetObject, name, args);
                        }
                );
                break;

            case FIELD_READ:
                context = new JavetCallbackContext(
                        name,
                        JavetCallbackType.DirectCallGetterAndThis,
                        (IJavetDirectCallable.GetterAndThis)(V8Value _this)-> {
                            if(!(_this instanceof V8ValueObject object)){
                                throw new RuntimeException("Illegal invocation");
                            }

                            Object targetObject = converter.getNativeObject(object);

                            if(targetObject == null){
                                throw new RuntimeException("Illegal invocation");
                            }

                            if(!className.isAssignableFrom(targetObject.getClass())) {
                                throw new RuntimeException("Illegal invocation");
                            }

                            return converter.toV8Value(runtime, accessor.get(targetObject, name));
                        }
                );
                break;
            case FIELD_WRITE:
                context = new JavetCallbackContext(
                        name,
                        JavetCallbackType.DirectCallSetterAndThis,
                        (IJavetDirectCallable.SetterAndThis)(V8Value _this, V8Value value)-> {
                            if(!(_this instanceof V8ValueObject object)){
                                throw new RuntimeException("Illegal invocation");
                            }

                            Object targetObject = converter.getNativeObject(object);

                            if(targetObject == null){
                                throw new RuntimeException("Illegal invocation");
                            }

                            if(!className.isAssignableFrom(targetObject.getClass())) {
                                throw new RuntimeException("Illegal invocation");
                            }

                            return converter.toV8Value(runtime, accessor.set(targetObject, name, value));
                        }
                );
                break;
            default:
                throw new UnsupportedOperationException();
        }

        return context;
    }
}
