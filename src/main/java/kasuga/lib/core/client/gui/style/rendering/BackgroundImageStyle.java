package kasuga.lib.core.client.gui.style.rendering;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.render.ResourceImageProvider;
import kasuga.lib.core.client.gui.resource.ResourceStyle;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleType;
import kasuga.lib.core.client.gui.style.Styles;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BackgroundImageStyle extends Style<String> {

    public static final BackgroundImageStyle EMPTY = new BackgroundImageStyle();
    protected final String value;
    private Pair<ResourceLocation, ResourceLocation> location;

    private BackgroundImageStyle(){
        value = "";
    }

    public BackgroundImageStyle(String value) {
        this.value = value;
        try{
            this.location = ResourceStyle.parse(value);
        }catch (IllegalStateException e){}
    }

    @Override
    public boolean isValid(Map<StyleType<?>, Style<?>> origin) {
        return this.location != null;
    }

    @Override
    public StyleType<?> getType() {
        return Styles.BACKGROUND_IMAGE;
    }

    @Override
    public void apply(Node node) {
        if(!isValid(null))
            return;
        node.getBackground().setImage(new ResourceImageProvider(location.getSecond()));
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
