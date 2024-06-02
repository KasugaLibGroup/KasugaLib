package kasuga.lib.core.client.frontend.gui.styles.layout;

import kasuga.lib.core.client.frontend.common.style.Style;
import kasuga.lib.core.client.frontend.common.style.StyleTarget;
import kasuga.lib.core.client.frontend.common.style.StyleType;
import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaNode;
import kasuga.lib.core.client.frontend.gui.styles.PixelUnit;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class SizeStyle extends Style<Pair<Float, PixelUnit>, StyleTarget> {

    public final String original;
    public final Pair<Float,PixelUnit> value;
    private StyleTarget target;

    public SizeStyle(float value,PixelUnit unit){
        this.value = Pair.of(value,unit);
        this.original = unit.toString(value);
        this.createTarget();
    }

    public SizeStyle(String source){
        this.original = source;
        this.value = PixelUnit.parse(source);
        this.createTarget();
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

    public static SizeStyleType createType(BiConsumer<YogaNode,Pair<Float, PixelUnit>> consumer) {
        return new SizeStyleType(consumer);
    }

    private void createTarget() {
        this.target = StyleTarget.LAYOUT_NODE.create((node) -> {
            if (!this.isValid(null))
                return;
            getConsumer().accept(node,value);
        });
    }

    @Override
    public StyleTarget getTarget() {
        return target;
    }

    protected abstract BiConsumer<YogaNode,Pair<Float, PixelUnit>> getConsumer();

    public static class SizeStyleType implements StyleType<SizeStyle, StyleTarget>{
        private final BiConsumer<YogaNode,Pair<Float, PixelUnit>> consumer;

        SizeStyleType(BiConsumer<YogaNode,Pair<Float, PixelUnit>> accessor){
            this.consumer = accessor;
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

                @Override
                protected BiConsumer<YogaNode,Pair<Float, PixelUnit>> getConsumer() {
                    return consumer;
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
                @Override
                protected BiConsumer<YogaNode,Pair<Float, PixelUnit>> getConsumer() {
                    return consumer;
                }
            };
        }
    }
}