package kasuga.lib.core.client.frontend.gui.styles.node;

import kasuga.lib.core.client.frontend.common.style.ResourceStyle;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.rendering.BackgroundRenderer;
import kasuga.lib.core.client.frontend.rendering.ImageProvider;
import kasuga.lib.core.client.frontend.rendering.ImageProviders;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Objects;

public class BackgroundRenderTypeStyle extends Style<String, StyleTarget>{

    public static final StyleType<BackgroundRenderTypeStyle, StyleTarget> TYPE = SimpleNodeStyleType.of(BackgroundRenderTypeStyle::new, "simple");

    public static final BackgroundRenderTypeStyle EMPTY = new BackgroundRenderTypeStyle("default");
    protected final String value;
    private Pair<ResourceLocation, String> location;
    ImageProvider provider;

    public BackgroundRenderTypeStyle(String value) {
        this.value = value;
    }

    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return Objects.equals(value,"nine_slice") || Objects.equals(value,"nine_slice");
    }

    @Override
    public StyleType<?, StyleTarget> getType() {
        return TYPE;
    }

    @Override
    public StyleTarget getTarget() {
        return StyleTarget.GUI_DOM_NODE.create((node)-> {
            node.getBackgroundRenderer().setRenderMode(value == "simple" ? BackgroundRenderer.RenderMode.COMMON : BackgroundRenderer.RenderMode.NINE_SLICED);
        });
    }

    @Override
    public String getValueString() {
        return value;
    }

    @Override
    public String getValue() {
        return value;
    }
}