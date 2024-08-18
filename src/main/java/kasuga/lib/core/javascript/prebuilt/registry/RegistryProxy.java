package kasuga.lib.core.javascript.prebuilt.registry;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.ffi.ResourceLocationFFIHelper;
import kasuga.lib.core.javascript.registration.JavascriptPriorityRegistry;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;

public class RegistryProxy<T> implements Closeable {
    private final JavascriptContext context;
    JavascriptPriorityRegistry<T> target;
    Set<Pair<T,ResourceLocation>> registeredItems = new HashSet<>();
    RegistryProxy(JavascriptContext context, JavascriptPriorityRegistry<T> target){
        this.context = context;
        this.target = target;
    }

    public void register(ResourceLocation location, T item){
        registeredItems.add(Pair.of(item, location));
        target.register(context, location, item);
    }

    public void unregister(ResourceLocation location, T item){
        registeredItems.remove(Pair.of(item, location));
        target.unregister(context, location, item);
    }

    public void close(){
        for (Pair<T, ResourceLocation> registeredItem : registeredItems) {
            target.unregister(context, registeredItem.getSecond(), registeredItem.getFirst());
        }
        registeredItems.clear();
    }

    @HostAccess.Export
    public void register(Value location, Value item){
        ResourceLocation resourceLocation = ResourceLocationFFIHelper.fromValue(location);
        T localItem = target.fromValue(item);

        register(resourceLocation, localItem);
    }
}
