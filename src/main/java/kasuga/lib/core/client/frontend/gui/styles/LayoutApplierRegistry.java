package kasuga.lib.core.client.frontend.gui.styles;

import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.common.layouting.LayoutEngine;
import kasuga.lib.core.client.frontend.common.layouting.LayoutNode;
import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.layout.LayoutEngineType;
import kasuga.lib.core.client.frontend.gui.layout.LayoutNodeContext;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;
import kasuga.lib.core.util.data_type.Pair;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class LayoutApplierRegistry {

    private static final Logger LOGGER = KasugaLib.createLogger("LayoutApplierRegistry");
    private static final LayoutApplierRegistry INSTANCE = new LayoutApplierRegistry();

    private HashMap<Pair<LayoutEngineType<?>, StyleType<?, ?>>, LayoutApplier<LayoutEngine<?, ?>, LayoutNode, Style<?, ?>>> registry = new HashMap<>();

    public static LayoutApplierRegistry getInstance() {
        return INSTANCE;
    }

    private LayoutApplierRegistry() {}

    public void apply(LayoutNodeContext context, Style<?, ?> style) {
        LayoutEngine layoutEngine = context.engine();
        LayoutNode node = context.layoutNode();
        LayoutApplier<LayoutEngine<?, ?>, LayoutNode, Style<?, ?>> applier = registry.get(
                Pair.of(layoutEngine.getType(), style.getType())
        );

        if (applier == null) {
            LOGGER.warn("Cannot found applier for {} in {}", style.getType(), layoutEngine.getType());
            return;
        }

        applier.apply(layoutEngine, node, style);
    }

    public <
            T extends LayoutEngine<P, ?>,
            S extends Style<?, ?>,
            E extends StyleType<S, ?>,
            P extends LayoutNode
    > void register(LayoutEngineType<T> engineType, E styleType, LayoutApplier<T, P, S> applier) {
        registry.put(
                Pair.of(engineType, styleType),
                (LayoutApplier<LayoutEngine<?, ?>, LayoutNode, Style<?,?>>) applier
        );
    }

    public static interface LayoutApplier<T extends LayoutEngine<?, ?>, N extends LayoutNode, S extends Style<?, ?>> {
        public void apply(T layoutEngine, N node, S style);
    }
}
