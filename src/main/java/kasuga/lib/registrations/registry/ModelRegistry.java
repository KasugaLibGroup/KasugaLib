package kasuga.lib.registrations.registry;

import kasuga.lib.registrations.client.ModelReg;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class ModelRegistry {

    public final String namespace;
    public final HashMap<ResourceLocation, ModelReg> UNBAKED;
    public final HashMap<ResourceLocation, BakedModel> BAKED;

    public final SimpleRegistry registry;

    public ModelRegistry(String namespace, SimpleRegistry registry) {
        this.namespace = namespace;
        this.UNBAKED = new HashMap<>();
        this.BAKED = new HashMap<>();
        this.registry = registry;
    }

    public void register(ModelReg reg) {
        this.UNBAKED.put(reg.location(), reg);
    }

    public void putBakedIn(ResourceLocation location, BakedModel model) {
        this.BAKED.put(location, model);
    }

    public HashMap<ResourceLocation, ModelReg> getUnbaked() {
        return UNBAKED;
    }

    public void clearUnbaked() {
        this.UNBAKED.clear();
    }

    public BakedModel getModel(ResourceLocation location) {
        return BAKED.getOrDefault(location, null);
    }

    public HashMap<ResourceLocation, BakedModel> getBaked() {
        return BAKED;
    }
}
