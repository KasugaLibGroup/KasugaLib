package kasuga.lib.core.client.gui.style.layout;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.layout.yoga.YogaEdge;
import kasuga.lib.core.client.gui.style.PixelUnit;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleType;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Map;

public abstract class PositionStyle extends Style<Pair<Float, PixelUnit>> {
    public final String original;
    public final Pair<Float,PixelUnit> value;

    public PositionStyle(float value,PixelUnit unit){
        this.value = Pair.of(value,unit);
        this.original = unit.toString(value);
    }

    public PositionStyle(String source){
        this.original = source;
        this.value = PixelUnit.parse(source);
    }

    @Override
    public void apply(Node node){
        if(!this.isValid(null))
            return;
        switch (value.getSecond()){
            case NATIVE -> {
                    node.getLocatorNode().setPosition(getEdgeType(),value.getFirst());
                    System.out.println("Apply static("+ getEdgeType() +" = "+ value.getSecond().toString(value.getFirst())+")");
            }
            case PERCENTAGE -> {
                node.getLocatorNode().setPositionPercent(getEdgeType(), value.getFirst());
                System.out.println("Apply percentage(" + getEdgeType() + " = " + value.getSecond().toString(value.getFirst()) + ")");
            }
        }
        node.markReLayout();
    }

    @Override
    public boolean isValid(Map<StyleType<?>,Style<?>> styles) {
        return value != null && value.getSecond() != PixelUnit.INVALID;
    }

    protected abstract YogaEdge getEdgeType();

    public Pair<Float, PixelUnit> getValue(){
        return value;
    }

    @Override
    public String getValueString() {
        return value.getSecond().toString(value.getFirst());
    }

    public static PositionStyleType createType(YogaEdge edge){
        return new PositionStyleType(edge);
    }

    public static class PositionStyleType implements StyleType<PositionStyle>{

        private final YogaEdge edgeType;

        PositionStyleType(YogaEdge edgeType){
            this.edgeType = edgeType;
            EMPTY = create(0,PixelUnit.NATIVE);
        }

        public final PositionStyle EMPTY;

        @Override
        public PositionStyle getDefault() {
            return EMPTY;
        }

        @Override
        public PositionStyle create(String string) {
            PositionStyleType type = this;
            return new PositionStyle(string) {
                @Override
                protected YogaEdge getEdgeType() {
                    return edgeType;
                }

                @Override
                public StyleType<?> getType() {
                    return type;
                }
            };
        }

        public PositionStyle create(float value,PixelUnit unit){
            PositionStyleType type = this;
            return new PositionStyle(value,unit) {
                @Override
                protected YogaEdge getEdgeType() {
                    return edgeType;
                }

                @Override
                public StyleType<?> getType() {
                    return type;
                }
            };
        }
    }
}
