package kasuga.lib.mixins.mixin;

import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(MultiPackResourceManager.class)
public interface MultiPackResourceManagerAccessor {

    @Accessor(value = "namespacedManagers")
    public Map<String, FallbackResourceManager> getNamespacedManagers();

    @Accessor(value = "packs")
    public List<PackResources> getPacks();
}
