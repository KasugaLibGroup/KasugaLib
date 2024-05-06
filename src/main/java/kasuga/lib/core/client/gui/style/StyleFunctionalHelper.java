package kasuga.lib.core.client.gui.style;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.layout.yoga.YogaNode;

public class StyleFunctionalHelper {
    public static <T> StyleNodeAccessor<T> node(StyleNodeAccessor<T> nodeAccessor){
        return nodeAccessor;
    }

    public static <T> StyleYogaNodeAccessor<T> layout(StyleYogaNodeAccessor<T> layoutAccessor){
        return layoutAccessor;
    }

    public static <T> BothAccessor<T> both(StyleNodeAccessor<T> nodeAccessor,StyleYogaNodeAccessor<T> layoutAccessor){
        return new BothAccessor<T>() {
            @Override
            public void apply(Node node,T value) {
                nodeAccessor.apply(node,value);
            }

            @Override
            public void apply(YogaNode node,T value) {
                layoutAccessor.apply(node,value);
            }
        };
    }

    public static interface StyleAccessor<T>{}
    @FunctionalInterface

    public static interface StyleNodeAccessor<T> extends StyleAccessor<T>{
        void apply(Node node,T value);
    }
    @FunctionalInterface
    public static interface StyleYogaNodeAccessor<T> extends StyleAccessor<T>{
        void apply(YogaNode node,T value);
    }

    public static interface BothAccessor<T> extends StyleNodeAccessor<T>,StyleYogaNodeAccessor<T>{

    }
}
