package kasuga.lib.core.javascript.registration;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class RegistrationRegistry {

    public HashMap<ResourceLocation,JavascriptPriorityRegistry<?>> registries = new HashMap<>();
    public void register(ResourceLocation location, JavascriptPriorityRegistry<?> registry){
        registries.put(location, registry);
    }

    public JavascriptPriorityRegistry<?> getRegistry(ResourceLocation location){
        return registries.get(location);
    }

}
