package kasuga.lib.mixins.mixin.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ModelLoaderRegistry.class)
public interface ModelLoaderRegistryAccessor {

    @Accessor(value = "loaders")
    public Map<ResourceLocation, IModelLoader<?>> getLoaders();
}
