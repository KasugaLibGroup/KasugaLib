package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;
import kasuga.lib.core.client.frontend.gui.styles.PixelUnit;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Map;

public abstract class PositionStyle extends Style<Pair<Float, PixelUnit>, StyleTarget> {
    public final String original;
    public final Pair<Float,PixelUnit> value;

    public PositionStyle(float value,PixelUnit unit){
        this.value = Pair.of(value,unit);
        this.original = unit.toString(value);
        createTarget();
    }

    private void createTarget() {
        target = StyleTarget.LAYOUT_NODE.create((node)->{
            if(!this.isValid(null))
                return;
            switch (value.getSecond()){
                case NATIVE -> {
                    node.setPosition(getEdgeType(),value.getFirst());
                    System.out.println("Apply static("+ getEdgeType() +" = "+ value.getSecond().toString(value.getFirst())+")");
                }
                case PERCENTAGE -> {
                    node.setPositionPercent(getEdgeType(), value.getFirst());
                    System.out.println("Apply percentage(" + getEdgeType() + " = " + value.getSecond().toString(value.getFirst()) + ")");
                }
            }
        });
    }

    public PositionStyle(String source){
        this.original = source;
        this.value = PixelUnit.parse(source);
        createTarget();
    }

    @Override
    public boolean isValid(Map<StyleType<?,StyleTarget>,Style<?,StyleTarget>> styles) {
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

    static StyleTarget target;

    @Override
    public StyleTarget getTarget() {
        return target;
    }

    public static class PositionStyleType implements StyleType<PositionStyle,StyleTarget>{

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
                public StyleType<?,StyleTarget> getType() {
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
                public StyleType<?,StyleTarget> getType() {
                    return type;
                }
            };
        }
    }
}