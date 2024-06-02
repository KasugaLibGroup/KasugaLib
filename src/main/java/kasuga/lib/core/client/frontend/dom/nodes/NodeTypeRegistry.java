package kasuga.lib.core.client.frontend.dom.nodes;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NodeTypeRegistry<T extends DomNode> {
    Map<String, Supplier<T>> registry = new HashMap<>();

    public void register(String name, Supplier<T> constructor){
        registry.put(name,constructor);
    }

    public T create(String name){
        if(!registry.containsKey(name))
            throw new IllegalArgumentException("Registry error");
        return registry.get(name).get();
    }
}
