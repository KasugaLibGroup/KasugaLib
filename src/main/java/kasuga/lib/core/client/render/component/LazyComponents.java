package kasuga.lib.core.client.render.component;

import kasuga.lib.core.util.Resources;
import kasuga.lib.core.xml.IXmlObject;
import kasuga.lib.core.xml.XmlProcessor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class LazyComponents implements Component {
    private String key = "";
    private Style style;
    protected LazyComponents(String text) {
        this.key = key;
    }

    public static LazyComponents fromLiteral(String text) {
        return new LazyComponents(text);
    }

    public static LazyComponents fromTranslatable(String translatableKey) {
        Component component = Component.translatable(translatableKey);
        return new LazyComponents(component.getString());
    }

    public static LazyComponents fromXml(ResourceLocation location) throws IOException {
        Resource resource = Resources.getResource(location);
        IXmlObject<?> xml = XmlProcessor.decodeAndPull(resource.open());
        return new LazyComponents("");
    }

    public static LazyComponents empty() {
        return new LazyComponents("");
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    @Override
    public Style getStyle() {
        return style;
    }

    @Override
    public @NotNull ComponentContents getContents() {
        return new LiteralContents(key);
    }

    @Override
    public @NotNull List<Component> getSiblings() {
        return null;
    }

    @Override
    public @NotNull FormattedCharSequence getVisualOrderText() {
        return null;
    }


}
