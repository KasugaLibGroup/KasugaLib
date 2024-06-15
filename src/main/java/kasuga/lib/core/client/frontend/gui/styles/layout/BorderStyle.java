package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;

import java.util.Map;

public class BorderStyle extends Style<Float, StyleTarget> {
    public final String original;
    public final Float value;
    public final YogaEdge edge;
    public StyleTarget target;

    public BorderStyle(YogaEdge edge, float value){
        this.value = value;
        this.original = String.valueOf(value);
        createTarget();
        this.edge = edge;
    }

    public static StyleType<?, StyleTarget> createType(YogaEdge yogaEdge) {
        return new BorderStyleType(yogaEdge);
    }

    private void createTarget() {
        target = StyleTarget.LAYOUT_NODE.create((node)->{
            if(!this.isValid(null))
                return;
            node.setBorder(edge,value);
        });
    }

    public BorderStyle(YogaEdge edge,String source){
        Float _value;
        this.original = source;
        try{
            _value = Float.parseFloat(source);
        }catch (NumberFormatException e){
            _value = null;
        }
        this.edge = edge;
        this.value = _value;
        createTarget();
    }

    @Override
    public boolean isValid(Map<StyleType<?, StyleTarget>, Style<?, StyleTarget>> origin) {
        return value != null;
    }

    @Override
    public StyleType<?, StyleTarget> getType() {
        return null;
    }

    @Override
    public StyleTarget getTarget() {
        return target;
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
        public final YogaEdge edge;

        public BorderStyleType(YogaEdge edge){
            this.edge = edge;
            EMPTY = new BorderStyle(edge,0.0F);
        }

        @Override
        public BorderStyle getDefault() {
            return EMPTY;
        }

        @Override
        public BorderStyle create(String source) {
            return new BorderStyle(edge,source);
        }

        public BorderStyle create(float value) {
            return new BorderStyle(edge, value);
        }
    }
}
