package kasuga.lib.core.client.render.texture;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public interface RenderTypeBuilder {
    RenderType build(ResourceLocation location);
}
