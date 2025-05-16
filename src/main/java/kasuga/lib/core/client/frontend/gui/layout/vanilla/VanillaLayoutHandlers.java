package kasuga.lib.core.client.frontend.gui.layout.vanilla;

import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngines;
import kasuga.lib.core.client.frontend.gui.layout.yoga.YogaLayoutEngine;
import kasuga.lib.core.client.frontend.gui.layout.yoga.YogaLayoutNode;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.styles.AllGuiStyles;
import kasuga.lib.core.client.frontend.gui.styles.LayoutApplierRegistry;
import kasuga.lib.core.client.frontend.gui.styles.PixelUnit;

import java.util.Optional;

public class VanillaLayoutHandlers {
    protected static LayoutApplierRegistry lar = LayoutApplierRegistry.getInstance();

    public static void init(){
        register(AllGuiStyles.POSITION_TOP, (e, n, s)->{
            cast(n).ifPresent(($n) -> {
                float v = s.getValue().getFirst();
                if(s.getValue().getSecond() == PixelUnit.NATIVE)
                    $n.setEngineCoordinateTop(v);
            });
        });

        register(AllGuiStyles.POSITION_LEFT, (e, n, s)->{
            cast(n).ifPresent(($n) -> {
                float v = s.getValue().getFirst();
                if(s.getValue().getSecond() == PixelUnit.NATIVE)
                    $n.setEngineCoordinateLeft(v);
            });
        });

        register(AllGuiStyles.WIDTH, (e, n, s)->{
            cast(n).ifPresent(($n) -> {
                float v = s.getValue().getFirst();
                if(s.getValue().getSecond() == PixelUnit.NATIVE)
                    $n.setEngineCoordinateWidth(v);
            });
        });

        register(AllGuiStyles.HEIGHT, (e, n, s)->{
            cast(n).ifPresent(($n) -> {
                float v = s.getValue().getFirst();
                if(s.getValue().getSecond() == PixelUnit.NATIVE)
                    $n.setEngineCoordinateHeight(v);
            });
        });
    }

    protected static Optional<VanillaLayoutNode> cast(LayoutNode node){
        return node instanceof VanillaLayoutNode ? Optional.of(((VanillaLayoutNode) node)) : Optional.empty();
    }

    protected static <T extends Style<?, ?>> void register(StyleType<T, ?> type, LayoutApplierRegistry.LayoutApplier<VanillaLayoutEngine, VanillaLayoutNode, T> consumer) {
        lar.register(LayoutEngines.VANILLA, type, consumer);
    }
}
