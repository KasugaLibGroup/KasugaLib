package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.styles.PixelUnit;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class SizeStyle extends LayoutStyle<Pair<Float, PixelUnit>> {

    public final String original;
    public final Pair<Float,PixelUnit> value;
    private StyleTarget target;

    public SizeStyle(float value,PixelUnit unit){
        this.value = Pair.of(value,unit);
        this.original = unit.toString(value);
    }

    public SizeStyle(String source){
        this.original = source;
        this.value = PixelUnit.parse(source);
    }

    @Override
    public boolean isValid(Map<StyleType<?,StyleTarget>, Style<?,StyleTarget>> origin) {
        return value != null && value.getSecond() != PixelUnit.INVALID;
    }

    @Override
    public String getValueString() {
        return value.getSecond().toString(value.getFirst());
    }

    @Override
    public Pair<Float, PixelUnit> getValue() {
        return value;
    }

    public static SizeStyleType createType() {
        return new SizeStyleType();
    }

    public static class SizeStyleType implements StyleType<SizeStyle, StyleTarget>{
        SizeStyleType(){
            EMPTY = create(0,PixelUnit.NATIVE);
        }

        public final SizeStyle EMPTY;

        @Override
        public SizeStyle getDefault() {
            return EMPTY;
        }

        @Override
        public SizeStyle create(String string) {
            SizeStyle.SizeStyleType type = this;
            return new SizeStyle(string) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return type;
                }
            };
        }

        public SizeStyle create(float value, PixelUnit unit){
            SizeStyle.SizeStyleType type = this;
            return new SizeStyle(value,unit) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return type;
                }
            };
        }
    }
}