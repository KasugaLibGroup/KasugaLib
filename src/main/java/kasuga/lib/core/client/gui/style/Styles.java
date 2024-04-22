package kasuga.lib.core.client.gui.style;

import kasuga.lib.core.client.gui.layout.yoga.YogaEdge;
import kasuga.lib.core.client.gui.style.layout.PositionStyle;
import kasuga.lib.core.client.gui.style.layout.SizeStyle;
import kasuga.lib.core.client.gui.style.rendering.BackgroundImageStyle;
import kasuga.lib.core.client.gui.style.rendering.BackgroundUVStyle;
import kasuga.lib.core.client.gui.style.rendering.SimpleStyleType;

public class Styles {
    public static PositionStyle.PositionStyleType TOP =
            StyleRegistry.register("top",PositionStyle.createType(YogaEdge.TOP));

    public static PositionStyle.PositionStyleType LEFT =
            StyleRegistry.register("left",PositionStyle.createType(YogaEdge.LEFT));

    public static SizeStyle.SizeStyleType WIDTH =
            StyleRegistry.register("width",SizeStyle.createType((n,v)->{
                switch (v.getSecond()){
                    case NATIVE -> n.getLocatorNode().setWidth(v.getFirst());
                    case PERCENTAGE -> n.getLocatorNode().setWidthPercent(v.getFirst());
                }
            }));

    public static SizeStyle.SizeStyleType HEIGHT =
            StyleRegistry.register("height",SizeStyle.createType((n,v)->{
                switch (v.getSecond()){
                    case NATIVE -> n.getLocatorNode().setHeight(v.getFirst());
                    case PERCENTAGE -> n.getLocatorNode().setHeightPercent(v.getFirst());
                }
            }));

    public static SimpleStyleType<BackgroundImageStyle> BACKGROUND_IMAGE =
            StyleRegistry.register("backgroundImage",SimpleStyleType.of(BackgroundImageStyle::new, BackgroundImageStyle.EMPTY));

    public static SimpleStyleType<BackgroundUVStyle> BACKGROUND_UV =
            StyleRegistry.register("backgroundUV",SimpleStyleType.of(BackgroundUVStyle::new,BackgroundUVStyle.EMPTY));

    public static void init(){

    }
}
