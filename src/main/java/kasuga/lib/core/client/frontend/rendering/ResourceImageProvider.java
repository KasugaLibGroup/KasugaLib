package kasuga.lib.core.client.frontend.rendering;

import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.client.render.texture.WorldTexture;
import net.minecraft.resources.ResourceLocation;

public class ResourceImageProvider implements ImageProvider {
    private final ResourceLocation location;

    public ResourceImageProvider(ResourceLocation location){
        this.location = location;
    }
    @Override
    public WorldTexture getWorldTexture() {
        return new WorldTexture(location);
    }

    @Override
    public SimpleTexture getSimpleTexture() {
        return new SimpleTexture(location);
    }
}