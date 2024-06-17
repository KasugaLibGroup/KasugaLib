package kasuga.lib.core.client.frontend.common.style;

import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

import java.util.function.Consumer;
import java.util.function.Function;

public interface StyleTarget {
    public boolean canApplyTo(Object object);

    public void apply(Object object);

    public default void attemptApply(Object object){
        if(canApplyTo(object))
            apply(object);
    }

    public static class StyleTargetType<T>{
        protected final Function<Object,Boolean> filter;
        protected final Function<Object,T> transformer;

        public StyleTargetType(Function<Object, Boolean> filter, Function<Object, T> transformer) {
            this.filter = filter;
            this.transformer = transformer;
        }

        public StyleTarget create(Consumer<T> consumer){
            return new StyleTarget() {
                @Override
                public boolean canApplyTo(Object object) {
                    return filter.apply(object);
                }

                @Override
                public void apply(Object object) {
                    consumer.accept(transformer.apply(object));
                }
            };
        }
    }

    public static StyleTargetType<YogaNode> LAYOUT_NODE = new StyleTargetType<>(
            (n)->n instanceof YogaNode,
            (n)->(YogaNode) n
    );

    public static StyleTargetType<GuiDomNode> GUI_DOM_NODE = new StyleTargetType<>(
            (n)->n instanceof GuiDomNode,
            (n)->(GuiDomNode) n
    );
}
