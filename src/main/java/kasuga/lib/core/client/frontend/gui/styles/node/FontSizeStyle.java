package kasuga.lib.core.client.frontend.gui.styles.node;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.nodes.GuiTextNode;

import java.util.Map;

public class FontSizeStyle extends Style<Float, StyleTarget> {

    public static final StyleType<FontSizeStyle, StyleTarget> TYPE = SimpleNodeStyleType.of(FontSizeStyle::new, "0");

    float value;

    boolean valid;

    public FontSizeStyle(String size){
        try{
            value = Float.parseFloat(size);
            valid = true;
        }catch (NumberFormatException e){
            return;
        }
    }
    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return valid;
    }

    @Override
    public StyleType<?, StyleTarget> getType() {
        return TYPE;
    }

    @Override
    public StyleTarget getTarget() {
        return StyleTarget.GUI_DOM_NODE.create((node)->{
            node.fontSize.setSize((int)value);
        });
    }

    @Override
    public String getValueString() {
        return String.valueOf(value);
    }

    @Override
    public Float getValue() {
        return value;
    }
}
