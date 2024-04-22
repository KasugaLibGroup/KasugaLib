package kasuga.lib.core.client.gui.render;

import kasuga.lib.core.client.render.texture.SimpleTexture;
import kasuga.lib.core.client.render.texture.WorldTexture;

public interface ImageProvider {
    WorldTexture getWorldTexture();
    SimpleTexture getSimpleTexture();
}
