package kasuga.lib.core.client.frontend.gui.layout.yoga;

import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.layout.yoga.value.ValueMapperBuilder;
import kasuga.lib.core.client.frontend.gui.styles.AllGuiStyles;
import kasuga.lib.core.client.frontend.gui.styles.LayoutApplierRegistry;
import kasuga.lib.core.client.frontend.gui.styles.PixelUnit;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Optional;

public class YogaStyleHandlers {
    protected static LayoutApplierRegistry lar = LayoutApplierRegistry.getInstance();
    public static void init() {

        register(AllGuiStyles.POSITION_TOP, (e,n,s)->{
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setPosition(YogaEdge.TOP, v.getFirst());
                    case PERCENTAGE -> $n.setPositionPercent(YogaEdge.TOP, v.getFirst());
                }
            });
        });

        register(AllGuiStyles.POSITION_LEFT, (e,n,s)->{
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setPosition(YogaEdge.LEFT, v.getFirst());
                    case PERCENTAGE -> $n.setPositionPercent(YogaEdge.LEFT, v.getFirst());
                }
            });
        });

        register(AllGuiStyles.POSITION_RIGHT, (e,n,s)->{
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setPosition(YogaEdge.RIGHT, v.getFirst());
                    case PERCENTAGE -> $n.setPositionPercent(YogaEdge.RIGHT, v.getFirst());
                }
            });
        });

        register(AllGuiStyles.POSITION_BOTTOM, (e,n,s)->{
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setPosition(YogaEdge.BOTTOM, v.getFirst());
                    case PERCENTAGE -> $n.setPositionPercent(YogaEdge.BOTTOM, v.getFirst());
                }
            });
        });

        register(AllGuiStyles.HEIGHT, (e,n,s)->{
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setHeight(v.getFirst());
                    case PERCENTAGE -> $n.setHeightPercent(v.getFirst());
                }
            });
        });

        // Width style
        register(AllGuiStyles.WIDTH, (e,n,s) -> {
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setWidth(v.getFirst());
                    case PERCENTAGE -> $n.setWidthPercent(v.getFirst());
                }
            });
        });
        

        // Display type
        register(AllGuiStyles.DISPLAY_TYPE, (e,n,s) -> {
            cast(n).ifPresent($n -> $n.setDisplay(ValueMapperBuilder.DISPLAY_TYPE.get(s.getValue())));
        });

        // Position type
        register(AllGuiStyles.POSITION_TYPE, (e,n,s) -> {
            cast(n).ifPresent($n -> $n.setPositionType(ValueMapperBuilder.POSITION_TYPE.get(s.getValue())));
        });

        // Align styles

        register(AllGuiStyles.ALIGN_SELF, (e,n,s) -> {
            cast(n).ifPresent($n -> $n.setAlignSelf(ValueMapperBuilder.ALIGN_TYPE.get(s.getValue())));
        });

        register(AllGuiStyles.ALIGN_ITEMS, (e,n,s) -> {
            cast(n).ifPresent($n -> $n.setAlignItems(ValueMapperBuilder.ALIGN_TYPE.get(s.getValue())));
        });

        // Justify content
        register(AllGuiStyles.JUSTIFY_CONTENT, (e,n,s) -> {
            cast(n).ifPresent($n -> $n.setJustifyContent(ValueMapperBuilder.JUSTIFY_TYPE.get(s.getValue())));
        });

        // Flex styles
        register(AllGuiStyles.FLEX_DIRECTION, (e,n,s) -> {
            cast(n).ifPresent($n -> $n.setFlexDirection(ValueMapperBuilder.FLEX_DIRECTION.get(s.getValue())));
        });

        register(AllGuiStyles.FLEX_BASIS, (e,n,s) -> {
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setFlexBasis(v.getFirst());
                    case PERCENTAGE -> $n.setFlexBasisPercent(v.getFirst());
                }
            });
        });

        register(AllGuiStyles.BORDER_TOP, (e,n,s) -> {
            Float v = s.getValue();
            cast(n).ifPresent($n -> $n.setBorder(YogaEdge.TOP, v));
        });

        register(AllGuiStyles.BORDER_LEFT, (e,n,s) -> {
            Float v = s.getValue();
            cast(n).ifPresent($n -> $n.setBorder(YogaEdge.LEFT, v));
        });

        register(AllGuiStyles.BORDER_RIGHT, (e,n,s) -> {
            Float v = s.getValue();
            cast(n).ifPresent($n -> $n.setBorder(YogaEdge.RIGHT, v));
        });

        register(AllGuiStyles.BORDER_BOTTOM, (e,n,s) -> {
            Float v = s.getValue();
            cast(n).ifPresent($n -> $n.setBorder(YogaEdge.BOTTOM, v));
        });

        // Margin styles
        register(AllGuiStyles.MARGIN_TOP, (e,n,s) -> {
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setMargin(YogaEdge.TOP, v.getFirst());
                    case PERCENTAGE -> $n.setMargin(YogaEdge.TOP, v.getFirst());
                }
            });
        });

        register(AllGuiStyles.MARGIN_LEFT, (e,n,s) -> {
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setMargin(YogaEdge.LEFT, v.getFirst());
                    case PERCENTAGE -> $n.setMargin(YogaEdge.LEFT, v.getFirst());
                }
            });
        });

        register(AllGuiStyles.MARGIN_RIGHT, (e,n,s) -> {
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setMargin(YogaEdge.RIGHT, v.getFirst());
                    case PERCENTAGE -> $n.setMargin(YogaEdge.RIGHT, v.getFirst());
                }
            });
        });

        register(AllGuiStyles.MARGIN_BOTTOM, (e,n,s) -> {
            Pair<Float, PixelUnit> v = s.getValue();
            cast(n).ifPresent($n -> {
                switch (v.getSecond()) {
                    case NATIVE -> $n.setMargin(YogaEdge.BOTTOM, v.getFirst());
                    case PERCENTAGE -> $n.setMargin(YogaEdge.BOTTOM, v.getFirst());
                }
            });
        });


    }

    protected static Optional<YogaNode> cast(LayoutNode node){
        return node instanceof YogaLayoutNode ? Optional.of(((YogaLayoutNode) node).getYogaNode()) : Optional.empty();
    }

    protected static <T extends Style<?, ?>> void register(StyleType<T, ?> type, LayoutApplierRegistry.LayoutApplier<YogaLayoutEngine, YogaLayoutNode, T> consumer) {
        lar.register(LayoutEngines.YOGA, type, consumer);
    }
}
