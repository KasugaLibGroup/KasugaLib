package kasuga.lib.core.client.gui.components;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import kasuga.lib.core.client.gui.style.StyleRegistry;
import kasuga.lib.core.client.gui.style.StyleType;

import javax.json.JsonString;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SimpleComponentType<T extends Node> implements ComponentType<T> {
    private final BiConsumer<T, JsonObject> processor;
    private final Supplier<T> factory;

    public SimpleComponentType(Supplier<T> factory, BiConsumer<T, JsonObject> processor) {
        this.factory = factory;
        this.processor = processor;
    }
    public static <T extends Node> SimpleComponentType<T> of(Supplier<T> factory){
        return of(factory,null);
    }
    public static <T extends Node> SimpleComponentType<T> of(Supplier<T> factory, BiConsumer<T,JsonObject> processor){
        return new SimpleComponentType<>(factory,processor);
    }
    @Override
    public T create(JsonObject attributes) {
        T obj = factory.get();
        if(processor != null)
            processor.accept(obj,attributes);
        if(attributes.has("style")){
            JsonObject style = attributes.getAsJsonObject("style");
            for (Map.Entry<String, JsonElement> styleEntry : style.entrySet()) {
                JsonElement value = styleEntry.getValue();
                if(!value.isJsonPrimitive()){
                    continue;
                }
                JsonPrimitive primitive = (JsonPrimitive) value;
                if(!primitive.isString() && !primitive.isNumber() && !primitive.isBoolean()){
                    continue;
                }
                StyleType<?> type;
                if((type = StyleRegistry.getStyle(styleEntry.getKey()))==null)
                    continue;
                obj.style().addStyle(type.create(styleEntry.getValue().getAsString()));
            }
        }
        return obj;
    }
}
