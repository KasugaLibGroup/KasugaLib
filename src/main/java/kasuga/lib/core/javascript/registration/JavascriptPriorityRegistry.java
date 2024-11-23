package kasuga.lib.core.javascript.registration;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.JavascriptEngineContext;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;

import java.util.HashMap;

public abstract class JavascriptPriorityRegistry<T> {

    final HashMap<ResourceLocation,RegistrySet<Pair<T, JavascriptContext>,Integer>> registry = new HashMap<>();

    public RegistrySet<Pair<T, JavascriptContext>, Integer> getWritableRegistry(ResourceLocation location){
        synchronized(registry){
            return registry.computeIfAbsent(location,(l)->new RegistrySet<>());
        }
    }
    public void register(JavascriptContext self, ResourceLocation location, T item){
        int priority = 0;
        RegistrySet<Pair<T,JavascriptContext>,Integer> registrySet;

        getWritableRegistry(location).register(Pair.of(item,self),priority);
    }

    public void unregister(JavascriptContext self, ResourceLocation location, T item){
        int priority = 0;
        if(!registry.containsKey(location))
            return;
        RegistrySet<Pair<T,JavascriptContext>,Integer> rs = getWritableRegistry(location);
        rs.remove(Pair.of(item,self),priority);
        if(rs.empty()){
            registry.remove(location);
        }
    }

    public Pair<T,JavascriptContext> get(ResourceLocation location){
        if(registry.containsKey(location)){
            return registry.get(location).getPresent();
        }
        return null;
    }

    public Lazy<Pair<T,JavascriptContext>> lazy(ResourceLocation location){
        return Lazy.of(()->get(location));
    }

    public T fromValue(JavascriptValue value){
        throw new RuntimeException("Not Implemented");
    }

    public T fromValue(JavascriptContext context, JavascriptValue item) {
        return fromValue(item);
    }
}
