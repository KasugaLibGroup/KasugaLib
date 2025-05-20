package kasuga.lib.core.client.frontend.gui.styles;

import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;
import kasuga.lib.core.client.frontend.gui.styles.layout.*;
import kasuga.lib.core.client.frontend.gui.styles.node.*;

public class AllGuiStyles {
    public static PositionStyle.PositionStyleType POSITION_TOP = PositionStyle.createType();
    public static PositionStyle.PositionStyleType POSITION_LEFT = PositionStyle.createType();
    public static PositionStyle.PositionStyleType POSITION_RIGHT = PositionStyle.createType();
    public static PositionStyle.PositionStyleType POSITION_BOTTOM = PositionStyle.createType();
    public static SizeStyle.SizeStyleType WIDTH = SizeStyle.createType();
    public static SizeStyle.SizeStyleType HEIGHT = SizeStyle.createType();

    public static EnumStyle.EnumStyleType<DisplayType> DISPLAY_TYPE = EnumStyle.EnumStyleType.of(
            DisplayType::fromString,
            (v,i)->v != DisplayType.INVALID,
            DisplayType.UNSET
    );

    public static EnumStyle.EnumStyleType<PositionType> POSITION_TYPE = EnumStyle.EnumStyleType.of(
            PositionType::fromString,
            (v,i)->v != PositionType.INVALID,
            PositionType.STATIC
    );

    public static EnumStyle.EnumStyleType<AlignType> ALIGN_CONTENT = EnumStyle.EnumStyleType.of(
        AlignType::fromString,
        (v,i)->v != AlignType.INVALID,
        AlignType.AUTO
    );

    public static EnumStyle.EnumStyleType<AlignType> ALIGN_SELF = EnumStyle.EnumStyleType.of(
            AlignType::fromString,
            (v,i)->v != AlignType.INVALID,
            AlignType.AUTO
    );

    public static EnumStyle.EnumStyleType<AlignType> ALIGN_ITEMS = EnumStyle.EnumStyleType.of(
            AlignType::fromString,
            (v,i)->v != AlignType.INVALID,
            AlignType.AUTO
    );

    public static EnumStyle.EnumStyleType<JustifyType> JUSTIFY_CONTENT = EnumStyle.EnumStyleType.of(
            JustifyType::fromString,
            (v,i)->v != JustifyType.INVALID,
            JustifyType.FLEX_START
    );

    public static EnumStyle.EnumStyleType<FlexDirection> FLEX_DIRECTION = EnumStyle.EnumStyleType.of(
            FlexDirection::fromString,
            (v,i)->v != FlexDirection.INVALID,
            FlexDirection.INVALID
    );


    public static SizeStyle.SizeStyleType FLEX_BASIS = SizeStyle.createType();

    public static BorderStyle.BorderStyleType BORDER_TOP = BorderStyle.createType();
    public static BorderStyle.BorderStyleType BORDER_LEFT = BorderStyle.createType();
    public static BorderStyle.BorderStyleType BORDER_RIGHT = BorderStyle.createType();
    public static BorderStyle.BorderStyleType BORDER_BOTTOM = BorderStyle.createType();

    public static SizeStyle.SizeStyleType MARGIN_TOP = SizeStyle.createType();
    public static SizeStyle.SizeStyleType MARGIN_LEFT = SizeStyle.createType();
    public static SizeStyle.SizeStyleType MARGIN_RIGHT = SizeStyle.createType();
    public static SizeStyle.SizeStyleType MARGIN_BOTTOM = SizeStyle.createType();

    public static void register(GuiStyleRegistry styleRegistry) {
        styleRegistry.register("top", POSITION_TOP);
        styleRegistry.register("left", POSITION_LEFT);
        styleRegistry.register("right", POSITION_RIGHT);
        styleRegistry.register("bottom", POSITION_BOTTOM);
        styleRegistry.register("width", WIDTH);
        styleRegistry.register("height", HEIGHT);
        styleRegistry.register("displayType", DISPLAY_TYPE);
        styleRegistry.register("positionType", POSITION_TYPE);
        styleRegistry.register("alignContent", ALIGN_CONTENT);
        styleRegistry.register("alignSelf", ALIGN_SELF);
        styleRegistry.register("alignItems", ALIGN_ITEMS);
        styleRegistry.register("justifyContent", JUSTIFY_CONTENT);
        styleRegistry.register("flexDirection", FLEX_DIRECTION);
        styleRegistry.register("flexBasis", FLEX_BASIS);

        //Border
        styleRegistry.register("borderTop", BORDER_TOP);
        styleRegistry.register("borderLeft", BORDER_LEFT);
        styleRegistry.register("borderRight", BORDER_RIGHT);
        styleRegistry.register("borderBottom", BORDER_BOTTOM);

        // Margin
        styleRegistry.register("marginTop", MARGIN_TOP);
        styleRegistry.register("marginLeft", MARGIN_LEFT);
        styleRegistry.register("marginRight", MARGIN_RIGHT);
        styleRegistry.register("marginBottom", MARGIN_BOTTOM);

        //Background
        styleRegistry.register("backgroundUV", BackgroundUVStyle.TYPE);
        styleRegistry.register("backgroundImage", BackgroundImageStyle.TYPE);
        styleRegistry.register("backgroundFilterColor", BackgroundFilterColor.TYPE);
        styleRegistry.register("backgroundRenderType", BackgroundRenderTypeStyle.TYPE);
        styleRegistry.register("backgroundNineSliceParam", BackgroundNineSliceParam.TYPE);

        styleRegistry.register("fontSize", FontSizeStyle.TYPE);
        styleRegistry.register("zIndex", ZIndexStyle.TYPE);
    }
}
