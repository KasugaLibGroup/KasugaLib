package kasuga.lib.core.client.frontend.common.style;

import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.nodes.GuiDomNode;

public class StyleFunctionalHelper {
    public static <T> StyleNodeAccessor<T> node(StyleNodeAccessor<T> nodeAccessor){
        return nodeAccessor;
    }

    public static <T> StyleYogaNodeAccessor<T> layout(StyleYogaNodeAccessor<T> layoutAccessor){
        return layoutAccessor;
    }

    public static <T> BothAccessor<T> both(StyleNodeAccessor<T> nodeAccessor,StyleYogaNodeAccessor<T> layoutAccessor){
        return new BothAccessor<>() {
            @Override
            public void apply(GuiDomNode node, T value) {
                nodeAccessor.apply(node, value);
            }

            @Override
            public void apply(YogaNode node, T value) {
                layoutAccessor.apply(node, value);
            }
        };
    }

    public interface StyleAccessor<T>{}
    @FunctionalInterface

    public interface StyleNodeAccessor<T> extends StyleAccessor<T>{
        void apply(GuiDomNode node,T value);
    }
    @FunctionalInterface
    public interface StyleYogaNodeAccessor<T> extends StyleAccessor<T>{
        void apply(YogaNode node,T value);
    }

    public interface BothAccessor<T> extends StyleNodeAccessor<T>,StyleYogaNodeAccessor<T>{

    }
}
