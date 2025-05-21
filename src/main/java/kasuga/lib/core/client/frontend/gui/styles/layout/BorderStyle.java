package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;

import java.util.Map;

public abstract class BorderStyle extends LayoutStyle<Float> {
    public final String original;
    public final Float value;
    public StyleTarget target;

    public BorderStyle(float value){
        this.value = value;
        this.original = String.valueOf(value);
    }

    public static BorderStyleType createType() {
        return new BorderStyleType();
    }

    public BorderStyle(String source){
        Float _value;
        this.original = source;
        try{
            _value = Float.parseFloat(source);
        }catch (NumberFormatException e){
            _value = null;
        }
        this.value = _value;
    }

    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return value != null;
    }

    public Float getValue(){
        return value;
    }

    @Override
    public String getValueString() {
        return String.valueOf(value);
    }

    public static class BorderStyleType implements StyleType<BorderStyle, StyleTarget> {

        protected final BorderStyle EMPTY;

        public BorderStyleType(){
            EMPTY = new BorderStyle(0.0F){
                @Override
                public BorderStyleType getType() {
                    return BorderStyleType.this;
                }
            };
        }

        @Override
        public BorderStyle getDefault() {
            return EMPTY;
        }

        @Override
        public BorderStyle create(String source) {
            return new BorderStyle(source) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return BorderStyleType.this;
                }
            };
        }

        public BorderStyle create(float value) {
            return new BorderStyle(value) {
                @Override
                public StyleType<?, StyleTarget> getType() {
                    return BorderStyleType.this;
                }
            };
        }
    }
}
