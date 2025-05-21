package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.styles.PixelUnit;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Map;

public abstract class PositionStyle extends LayoutStyle<Pair<Float, PixelUnit>> {
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
    public boolean isValid(Map<StyleType<?,StyleTarget>,Style<?,StyleTarget>> styles) {
        return value != null && value.getSecond() != PixelUnit.INVALID;
    }

    public Pair<Float, PixelUnit> getValue(){
        return value;
    }

    @Override
    public String getValueString() {
        return value.getSecond().toString(value.getFirst());
    }

    public static PositionStyleType createType(){
        return new PositionStyleType();
    }

    public static class PositionStyleType implements StyleType<PositionStyle,StyleTarget>{

        PositionStyleType(){
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
                public StyleType<?,StyleTarget> getType() {
                    return type;
                }
            };
        }

        public PositionStyle create(float value,PixelUnit unit){
            PositionStyleType type = this;
            return new PositionStyle(value,unit) {

                @Override
                public StyleType<?,StyleTarget> getType() {
                    return type;
                }
            };
        }
    }
}