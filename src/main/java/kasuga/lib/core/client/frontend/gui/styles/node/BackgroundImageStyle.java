package kasuga.lib.core.client.frontend.gui.styles.node;

import kasuga.lib.core.client.frontend.common.style.ResourceStyle;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.rendering.ImageProvider;
import kasuga.lib.core.client.frontend.rendering.ImageProviders;
import kasuga.lib.core.client.frontend.rendering.ResourceImageProvider;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BackgroundImageStyle extends Style<String, StyleTarget>{

    public static final StyleType<BackgroundImageStyle, StyleTarget> TYPE = SimpleNodeStyleType.of(BackgroundImageStyle::new, "");

    public static final BackgroundImageStyle EMPTY = new BackgroundImageStyle();
    protected final String value;
    private Pair<ResourceLocation, String> location;
    ImageProvider provider;

    private BackgroundImageStyle(){
        value = "";
    }

    public BackgroundImageStyle(String value) {
        this.value = value;
        try{
            this.location = ResourceStyle.parse(value);
            this.provider = ImageProviders.get(location);
        }catch (RuntimeException e){}
    }

    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return this.location != null && provider != null;
    }

    @Override
    public StyleType<?, StyleTarget> getType() {
        return TYPE;
    }

    @Override
    public StyleTarget getTarget() {
        return StyleTarget.GUI_DOM_NODE.create((node)-> {
            node.getBackgroundRenderer().setImage(provider);
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