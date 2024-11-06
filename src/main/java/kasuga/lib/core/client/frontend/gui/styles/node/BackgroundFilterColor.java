package kasuga.lib.core.client.frontend.gui.styles.node;

import kasuga.lib.core.client.frontend.common.style.ResourceStyle;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.rendering.ImageProvider;
import kasuga.lib.core.client.frontend.rendering.ImageProviders;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BackgroundFilterColor extends Style<SimpleColor, StyleTarget>{

    public static final StyleType<BackgroundFilterColor, StyleTarget> TYPE = SimpleNodeStyleType.of(BackgroundFilterColor::new, "");

    public static final BackgroundFilterColor EMPTY = new BackgroundFilterColor();
    protected final String value;

    protected final SimpleColor color;

    private BackgroundFilterColor(){
        value = "";
        this.color = SimpleColor.BLACK;
    }

    public BackgroundFilterColor(String value) {
        this.value = value;
        this.color = SimpleColor.fromHexString(value);
    }

    public BackgroundFilterColor(SimpleColor color) {
        this.value = color.toHexString();
        this.color = color;
    }

    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return true;
    }

    @Override
    public StyleType<?, StyleTarget> getType() {
        return TYPE;
    }

    @Override
    public StyleTarget getTarget() {
        return StyleTarget.GUI_DOM_NODE.create((node)-> {
            node.getBackgroundRenderer().setColor(color);
        });
    }

    @Override
    public String getValueString() {
        return value;
    }

    @Override
    public SimpleColor getValue() {
        return color;
    }
}