package kasuga.lib.mixins.mixin;

import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReloadableResourceManager.class)
public interface ReloadableResourceManagerAccessor {

    @Accessor(value = "resources")
    public CloseableResourceManager getResources();
}
