package kasuga.lib.core.client.gui.style;

import kasuga.lib.core.client.gui.layout.yoga.YogaEdge;
import kasuga.lib.core.client.gui.layout.yoga.YogaFlexDirection;
import kasuga.lib.core.client.gui.style.layout.*;
import kasuga.lib.core.client.gui.style.rendering.BackgroundImageStyle;
import kasuga.lib.core.client.gui.style.rendering.BackgroundUVStyle;
import kasuga.lib.core.client.gui.style.rendering.SimpleStyleType;

public class Styles {
    public static PositionStyle.PositionStyleType TOP =
            StyleRegistry.register("top",PositionStyle.createType(YogaEdge.TOP));

    public static PositionStyle.PositionStyleType LEFT =
            StyleRegistry.register("left",PositionStyle.createType(YogaEdge.LEFT));

    public static SizeStyle.SizeStyleType WIDTH =
            StyleRegistry.register("width",SizeStyle.createType(StyleFunctionalHelper.layout((n,v)->{
                switch (v.getSecond()){
                    case NATIVE -> n.setWidth(v.getFirst());
                    case PERCENTAGE -> n.setWidthPercent(v.getFirst());
                }
            })));

    public static SizeStyle.SizeStyleType HEIGHT =
            StyleRegistry.register("height",SizeStyle.createType(StyleFunctionalHelper.layout((n,v)->{
                switch (v.getSecond()){
                    case NATIVE -> n.setHeight(v.getFirst());
                    case PERCENTAGE -> n.setHeightPercent(v.getFirst());
                }
            })));

    public static SimpleStyleType<BackgroundImageStyle> BACKGROUND_IMAGE =
            StyleRegistry.register("backgroundImage",SimpleStyleType.of(BackgroundImageStyle::new, BackgroundImageStyle.EMPTY));

    public static SimpleStyleType<BackgroundUVStyle> BACKGROUND_UV =
            StyleRegistry.register("backgroundUV",SimpleStyleType.of(BackgroundUVStyle::new,BackgroundUVStyle.EMPTY));


    public static EnumStyle.EnumStyleType<PositionType> POSITION_TYPE =
            StyleRegistry.register("positionType", EnumStyle.EnumStyleType.of(
                    PositionType::fromString,
                    (v,i)->v!=PositionType.INVALID,
                    StyleFunctionalHelper.layout((w,i)->w.setPositionType(i.getValue())),
                    PositionType.STATIC
            ));

    public static EnumStyle.EnumStyleType<DisplayType> DISPLAY_TYPE =
            StyleRegistry.register("displayType", EnumStyle.EnumStyleType.of(
                    DisplayType::fromString,
                    (v,i)->v!=DisplayType.INVALID,
                    StyleFunctionalHelper.layout((w,i)->w.setDisplay(i.getValue())),
                    DisplayType.UNSET
            ));

    public static EnumStyle.EnumStyleType<AlignType> ALIGN_CONTENT =
            StyleRegistry.register("alignContent", EnumStyle.EnumStyleType.of(
                    AlignType::fromString,
                    (v,i)->v!=AlignType.INVALID,
                    StyleFunctionalHelper.layout((w,i)->w.setAlignContent(i.getValue())),
                    AlignType.AUTO
            ));

    public static EnumStyle.EnumStyleType<AlignType> ALIGN_SELF =
            StyleRegistry.register("alignSelf", EnumStyle.EnumStyleType.of(
                    AlignType::fromString,
                    (v,i)->v!=AlignType.INVALID,
                    StyleFunctionalHelper.layout((w,i)->w.setAlignSelf(i.getValue())),
                    AlignType.AUTO
            ));

    public static EnumStyle.EnumStyleType<AlignType> ALIGN_ITEMS =
            StyleRegistry.register("alignItems", EnumStyle.EnumStyleType.of(
                    AlignType::fromString,
                    (v,i)->v!=AlignType.INVALID,
                    StyleFunctionalHelper.layout((w,i)->w.setAlignItems(i.getValue())),
                    AlignType.AUTO
            ));

    public static EnumStyle.EnumStyleType<JustifyType> JUSTIFY_CONTENT =
            StyleRegistry.register("justifyContent", EnumStyle.EnumStyleType.of(
                    JustifyType::fromString,
                    (v,i)->v!=JustifyType.INVALID,
                    StyleFunctionalHelper.layout((w,i)->w.setJustifyContent(i.getValue())),
                    JustifyType.FLEX_START
            ));

    public static EnumStyle.EnumStyleType<FlexDirection> FLEX_DIRECTION =
            StyleRegistry.register("flexDirection", EnumStyle.EnumStyleType.of(
                    FlexDirection::fromString,
                    (v,i)->v!=FlexDirection.INVALID,
                    StyleFunctionalHelper.layout((w,i)->w.setFlexDirection(i.getValue())),
                    FlexDirection.INVALID
            ));



    public static void init(){

    }
}
