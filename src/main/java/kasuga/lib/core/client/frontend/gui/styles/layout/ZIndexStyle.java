package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.styles.PixelUnit;
import kasuga.lib.core.client.frontend.gui.styles.node.FontSizeStyle;
import kasuga.lib.core.client.frontend.gui.styles.node.SimpleNodeStyleType;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Map;
import java.util.function.BiConsumer;

public class ZIndexStyle extends Style<Integer, StyleTarget> {

    public static final StyleType<ZIndexStyle, StyleTarget> TYPE = SimpleNodeStyleType.of(ZIndexStyle::new, "0");

    private final Integer integerValue;

    ZIndexStyle(String string){
        this.integerValue = Integer.parseInt(string);
    }

    ZIndexStyle(Integer integerValue){
        this.integerValue = integerValue;
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
        return StyleTarget.GUI_DOM_NODE.create((domNode)->{
            domNode.setZIndex(integerValue);
        });
    }
    @Override
    public String getValueString() {
        return integerValue.toString();
    }

    @Override
    public Integer getValue() {
        return integerValue;
    }
}
