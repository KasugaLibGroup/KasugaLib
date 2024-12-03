package kasuga.lib.core.client.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import java.util.HashMap;

public class NamedRenderTypeManager {

    public static NamedRenderTypeManager INSTANCE = new NamedRenderTypeManager();

    private final HashMap<ResourceLocation, RenderType> types;

    public NamedRenderTypeManager() {
        this.types = new HashMap<>();
    }

    public RenderType getType(ResourceLocation location) {
        return types.getOrDefault(location, RenderType.solid());
    }

    public static RenderType get(ResourceLocation location) {
        return INSTANCE.getType(location);
    }

    public void put(ResourceLocation location, RenderType type) {
        this.types.put(location, type);
    }
}
