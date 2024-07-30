package kasuga.lib.core.client.frontend.rendering;

import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.client.render.texture.StaticImage;
import kasuga.lib.core.client.render.texture.WorldTexture;

public interface ImageProvider {
    WorldTexture getWorldTexture();
    SimpleTexture getSimpleTexture();
    StaticImage getImage();
}