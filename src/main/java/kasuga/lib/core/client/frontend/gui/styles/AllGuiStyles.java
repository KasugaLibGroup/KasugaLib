package kasuga.lib.core.client.frontend.gui.styles;

import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;
import kasuga.lib.core.client.frontend.gui.styles.layout.DisplayType;
import kasuga.lib.core.client.frontend.gui.styles.layout.EnumStyle;
import kasuga.lib.core.client.frontend.gui.styles.layout.PositionStyle;
import kasuga.lib.core.client.frontend.gui.styles.layout.SizeStyle;

public class AllGuiStyles {
    public static void register(GuiStyleRegistry styleRegistry) {
        styleRegistry.register("top", PositionStyle.createType(YogaEdge.TOP));
        styleRegistry.register("left", PositionStyle.createType(YogaEdge.LEFT));

        styleRegistry.register("width", SizeStyle.createType((n,v)->{
            switch (v.getSecond()){
                case NATIVE -> n.setWidth(v.getFirst());
                case PERCENTAGE -> n.setWidthPercent(v.getFirst());
            }
        }));

        styleRegistry.register("height", SizeStyle.createType((n,v)->{
            switch (v.getSecond()){
                case NATIVE -> n.setHeight(v.getFirst());
                case PERCENTAGE -> n.setHeightPercent(v.getFirst());
            }
        }));

        styleRegistry.register("displayType", EnumStyle.EnumStyleType.of(
                DisplayType::fromString,
                (v,i)->v != DisplayType.INVALID,
                (v)->StyleTarget.LAYOUT_NODE.create((node)->{
                    node.setDisplay(v.getValue());
                }),
                DisplayType.UNSET
        ));
    }
}
