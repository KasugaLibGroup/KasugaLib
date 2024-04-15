package kasuga.lib.core.client.gui.attributes;

import java.util.HashMap;

public class AttributesRegistry {

    protected static final HashMap<String,AttributeType<?>> registry = new HashMap<>();
    protected static final HashMap<AttributeType<?>,String> name_registry = new HashMap<>();

    public static <T> AttributeType<T> register(String attributeName, AttributeType<T> attributeType){
        if(registry.containsKey(attributeName) || name_registry.containsKey(attributeType)){
            throw new IllegalStateException("Attribute "+attributeName+" has already registered in the registry");
        }
        registry.put(attributeName,attributeType);
        name_registry.put(attributeType,attributeName);
        return attributeType;
    }

    public static AttributeType<?> getRegistryItem(String attributeName){
        return registry.get(attributeName);
    }

    public static String getRegistryKey(AttributeType<?> attributeType){
        return name_registry.get(attributeType);
    }
}
