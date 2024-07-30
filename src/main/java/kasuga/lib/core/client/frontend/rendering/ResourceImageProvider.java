package kasuga.lib.core.client.frontend.rendering;

import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.client.render.texture.StaticImage;
import kasuga.lib.core.client.render.texture.WorldTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

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

    @Override
    public StaticImage getImage() {
        try {
            return StaticImage.createImage(location).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}