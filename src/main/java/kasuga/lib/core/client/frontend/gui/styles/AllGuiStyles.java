package kasuga.lib.core.client.frontend.gui.styles;

import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;
import kasuga.lib.core.client.frontend.gui.styles.layout.*;

public class AllGuiStyles {
    public static void register(GuiStyleRegistry styleRegistry) {
        //Position Types
        styleRegistry.register("left", PositionStyle.createType(YogaEdge.LEFT));
        styleRegistry.register("top", PositionStyle.createType(YogaEdge.TOP));
        styleRegistry.register("right", PositionStyle.createType(YogaEdge.RIGHT));
        styleRegistry.register("bottom", PositionStyle.createType(YogaEdge.BOTTOM));
        styleRegistry.register("start", PositionStyle.createType(YogaEdge.START));
        styleRegistry.register("end", PositionStyle.createType(YogaEdge.END));
        styleRegistry.register("horizontal", PositionStyle.createType(YogaEdge.HORIZONTAL));
        styleRegistry.register("vertical", PositionStyle.createType(YogaEdge.VERTICAL));
        styleRegistry.register("all", PositionStyle.createType(YogaEdge.ALL));

        styleRegistry.register("width", SizeStyle.createType((n, v) -> {
            switch (v.getSecond()) {
                case NATIVE -> n.setWidth(v.getFirst());
                case PERCENTAGE -> n.setWidthPercent(v.getFirst());
            }
        }));

        styleRegistry.register("height", SizeStyle.createType((n, v) -> {
            switch (v.getSecond()) {
                case NATIVE -> n.setHeight(v.getFirst());
                case PERCENTAGE -> n.setHeightPercent(v.getFirst());
            }
        }));

        styleRegistry.register("alignType", EnumStyle.EnumStyleType.of(
                AlignType::fromString,
                (v, i) -> v != AlignType.INVALID,
                (v) -> StyleTarget.LAYOUT_NODE.create((node) -> {
                    node.setAlignItems(v.getValue());
                }),
                AlignType.INVALID
        ));

        styleRegistry.register("displayType", EnumStyle.EnumStyleType.of(
                DisplayType::fromString,
                (v, i) -> v != DisplayType.INVALID,
                (v) -> StyleTarget.LAYOUT_NODE.create((node) -> {
                    node.setDisplay(v.getValue());
                }),
                DisplayType.UNSET
        ));

        styleRegistry.register("flexDirection", EnumStyle.EnumStyleType.of(
                FlexDirection::fromString,
                (v, i) -> v != FlexDirection.INVALID,
                (v) -> StyleTarget.LAYOUT_NODE.create((node) -> {
                    node.setFlexDirection(v.getValue());
                }),
                FlexDirection.INVALID
        ));

        styleRegistry.register("justifyType", EnumStyle.EnumStyleType.of(
                JustifyType::fromString,
                (v, i) -> v != JustifyType.INVALID,
                (v) -> StyleTarget.LAYOUT_NODE.create((node) -> {
                    node.setJustifyContent(v.getValue());
                }),
                JustifyType.INVALID
        ));
    }
}
