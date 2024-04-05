package kasuga.lib.core.client.gui;

import kasuga.lib.core.client.render.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;

public interface IBackground {
    void setBackground(SimpleTexture texture);
    void setBackground(ResourceLocation location);
    SimpleTexture getBackground();
    boolean hasBackground();
}
