package kasuga.lib.core.client.frontend.gui.styles;

import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;
import kasuga.lib.core.client.frontend.gui.styles.layout.*;
import kasuga.lib.core.client.frontend.gui.styles.node.*;

public class AllGuiStyles {
    public static void register(GuiStyleRegistry styleRegistry) {
        styleRegistry.register("top", PositionStyle.createType(YogaEdge.TOP));
        styleRegistry.register("left", PositionStyle.createType(YogaEdge.LEFT));
        styleRegistry.register("right", PositionStyle.createType(YogaEdge.RIGHT));
        styleRegistry.register("bottom", PositionStyle.createType(YogaEdge.BOTTOM));

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

        styleRegistry.register("positionType", EnumStyle.EnumStyleType.of(
                PositionType::fromString,
                (v,i)->v != PositionType.INVALID,
                (v)->StyleTarget.LAYOUT_NODE.create((node)->{
                    node.setPositionType(v.getValue());
                }),
                // TODO: NEED REVIEW
                PositionType.ABSOLUTE
        ));

        styleRegistry.register("alignContent", EnumStyle.EnumStyleType.of(
                AlignType::fromString,
                (v,i)->v != AlignType.INVALID,
                (v)->StyleTarget.LAYOUT_NODE.create((node)->{
                    node.setAlignContent(v.getValue());
                }),
                AlignType.AUTO
        ));

        styleRegistry.register("alignSelf", EnumStyle.EnumStyleType.of(
                AlignType::fromString,
                (v,i)->v != AlignType.INVALID,
                (v)->StyleTarget.LAYOUT_NODE.create((node)->{
                    node.setAlignSelf(v.getValue());
                }),
                AlignType.AUTO
        ));

        styleRegistry.register("alignItems", EnumStyle.EnumStyleType.of(
                AlignType::fromString,
                (v,i)->v != AlignType.INVALID,
                (v)->StyleTarget.LAYOUT_NODE.create((node)->{
                    node.setAlignItems(v.getValue());
                }),
                AlignType.AUTO
        ));

        styleRegistry.register("justifyContent", EnumStyle.EnumStyleType.of(
                JustifyType::fromString,
                (v,i)->v != JustifyType.INVALID,
                (v)->StyleTarget.LAYOUT_NODE.create((node)->{
                    node.setJustifyContent(v.getValue());
                }),
                JustifyType.FLEX_START
        ));

        styleRegistry.register("flexDirection", EnumStyle.EnumStyleType.of(
                FlexDirection::fromString,
                (v,i)->v != FlexDirection.INVALID,
                (v)->StyleTarget.LAYOUT_NODE.create((node)->{
                    node.setFlexDirection(v.getValue());
                }),
                FlexDirection.INVALID
        ));

        styleRegistry.register("flexBasis", SizeStyle.createType((n,v)->{
            switch (v.getSecond()){
                case NATIVE -> n.setFlexBasis(v.getFirst());
                case PERCENTAGE -> n.setFlexBasisPercent(v.getFirst());
            }
        }));

        //Border
        styleRegistry.register("borderTop", BorderStyle.createType(YogaEdge.TOP));
        styleRegistry.register("borderLeft", BorderStyle.createType(YogaEdge.LEFT));
        styleRegistry.register("borderRight", BorderStyle.createType(YogaEdge.RIGHT));
        styleRegistry.register("borderBottom", BorderStyle.createType(YogaEdge.BOTTOM));

        // Margin
        styleRegistry.register("marginTop", SizeStyle.createType((n,v)->{
            switch (v.getSecond()){
                case NATIVE -> n.setMargin(YogaEdge.TOP,v.getFirst());
                case PERCENTAGE -> n.setMarginPercent(YogaEdge.TOP,v.getFirst());
            }
        }));

        styleRegistry.register("marginLeft", SizeStyle.createType((n,v)->{
            switch (v.getSecond()){
                case NATIVE -> n.setMargin(YogaEdge.LEFT,v.getFirst());
                case PERCENTAGE -> n.setMarginPercent(YogaEdge.LEFT,v.getFirst());
            }
        }));

        styleRegistry.register("marginRight", SizeStyle.createType((n,v)->{
            switch (v.getSecond()){
                case NATIVE -> n.setMargin(YogaEdge.RIGHT,v.getFirst());
                case PERCENTAGE -> n.setMarginPercent(YogaEdge.RIGHT,v.getFirst());
            }
        }));

        styleRegistry.register("marginBottom", SizeStyle.createType((n,v)->{
            switch (v.getSecond()){
                case NATIVE -> n.setMargin(YogaEdge.BOTTOM,v.getFirst());
                case PERCENTAGE -> n.setMarginPercent(YogaEdge.BOTTOM,v.getFirst());
            }
        }));

        styleRegistry.register("backgroundUV", BackgroundUVStyle.TYPE);
        styleRegistry.register("backgroundImage", BackgroundImageStyle.TYPE);
        styleRegistry.register("backgroundFilterColor", BackgroundFilterColor.TYPE);
        styleRegistry.register("backgroundRenderType", BackgroundRenderTypeStyle.TYPE);
        styleRegistry.register("backgroundNineSliceParam", BackgroundNineSliceParam.TYPE);

        styleRegistry.register("fontSize", FontSizeStyle.TYPE);
        styleRegistry.register("zIndex", ZIndexStyle.TYPE);
    }
}
