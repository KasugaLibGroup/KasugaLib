package kasuga.lib.core.client.render.font;

import kasuga.lib.KasugaLib;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class Font {
    private final ResourceLocation location;
    private Style style = Style.EMPTY;
    public Font(ResourceLocation location) {
        this.location = location;
        style.withColor(0xffffff);
        if(KasugaLib.STACKS.isTextureRegistryFired()) {loadFont();}
        else KasugaLib.STACKS.fontRegistry().stackIn(this);
    }

    public Font() {
        this.location = null;
    }

    public Style getFont() {
        return style;
    }

    public void loadFont() {
        style = Style.EMPTY.withFont(location);
    }

    public ResourceLocation getLocation() {
        return location;
    }
}
