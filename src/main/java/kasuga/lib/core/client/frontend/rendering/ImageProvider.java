package kasuga.lib.core.client.frontend.rendering;

import kasuga.lib.core.client.render.texture.old.SimpleTexture;
import kasuga.lib.core.client.render.texture.old.WorldTexture;

public interface ImageProvider {
    WorldTexture getWorldTexture();
    SimpleTexture getSimpleTexture();
}