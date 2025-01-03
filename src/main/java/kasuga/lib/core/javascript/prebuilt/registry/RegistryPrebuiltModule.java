package kasuga.lib.core.javascript.prebuilt.registry;

import kasuga.lib.core.javascript.JavascriptContext;
import kasuga.lib.core.javascript.engine.annotations.HostAccess;
import kasuga.lib.core.javascript.engine.JavascriptValue;
import kasuga.lib.core.javascript.ffi.ResourceLocationFFIHelper;
import kasuga.lib.core.javascript.prebuilt.PrebuiltModule;
import kasuga.lib.core.javascript.registration.JavascriptPriorityRegistry;
import kasuga.lib.core.javascript.registration.RegistrationRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RegistryPrebuiltModule extends PrebuiltModule {
    private final RegistrationRegistry registry;
    private final JavascriptContext context;

    private final Map<ResourceLocation, RegistryProxy<?>> map = new HashMap<>();

    public RegistryPrebuiltModule(JavascriptContext runtime) {
        super(runtime);
        this.registry = runtime.getSidedRegistry();
        this.context = runtime;
    }

    public RegistryProxy<?> getRegistry(ResourceLocation location){
        JavascriptPriorityRegistry<?> registryEntry = registry.getRegistry(location);
        if(registryEntry == null)
            throw new IllegalStateException("Illegal State: registry "+location.toString()+" not loaded");
        return map.computeIfAbsent(location,(l)->new RegistryProxy<>(context,registryEntry));
    }

    @HostAccess.Export
    public RegistryProxy<?> getRegistry(JavascriptValue value){
        return getRegistry(ResourceLocationFFIHelper.fromValue(value));
    }


    @Override
    public void close(){
        for (RegistryProxy<?> proxy : map.values()) {
            proxy.close();
        }
        map.clear();
    }
}
