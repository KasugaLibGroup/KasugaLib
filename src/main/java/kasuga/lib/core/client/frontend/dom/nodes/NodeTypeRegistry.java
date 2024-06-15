package kasuga.lib.core.client.frontend.dom.nodes;

import kasuga.lib.core.client.frontend.gui.GuiContext;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class NodeTypeRegistry<T extends DomNode> {
    Map<String, Function<GuiContext,T>> registry = new HashMap<>();

    public void register(String name, Function<GuiContext,T> constructor){
        registry.put(name,constructor);
    }

    public T create(String name,GuiContext context){
        if(!registry.containsKey(name))
            throw new IllegalArgumentException("Registry error");
        return registry.get(name).apply(context);
    }
}
