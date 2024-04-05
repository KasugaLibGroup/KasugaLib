package kasuga.lib.registrations.registry;

import kasuga.lib.core.annos.Inner;
import kasuga.lib.registrations.client.ModelReg;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

/**
 * ModelRegistry is the registry of KasugaLib style model registration.
 * KasugaLib provides {@link kasuga.lib.core.client.render.model.SimpleModel} and
 * {@link kasuga.lib.core.client.render.model.MultiPartModel} for data-gen model definition and quick
 * animation development.
 */
@Inner
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

    /**
     * register the model into this registry
     * @param reg the model to be registered.
     */
    public void register(ModelReg reg) {
        this.UNBAKED.put(reg.location(), reg);
    }

    @Inner
    public void putBakedIn(ResourceLocation location, BakedModel model) {
        this.BAKED.put(location, model);
    }

    @Inner
    public HashMap<ResourceLocation, ModelReg> getUnbaked() {
        return UNBAKED;
    }

    @Inner
    public void clearUnbaked() {
        this.UNBAKED.clear();
    }

    /**
     * getModel from this registry
     * @param location the location of model
     * @return the model
     */
    public BakedModel getModel(ResourceLocation location) {
        return BAKED.getOrDefault(location, null);
    }

    /**
     * get all baked models.
     * @return all baked models.
     */
    public HashMap<ResourceLocation, BakedModel> getBaked() {
        return BAKED;
    }
}
