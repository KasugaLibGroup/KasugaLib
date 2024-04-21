package kasuga.lib.core.client.gui.style.layout;

import kasuga.lib.core.client.gui.components.Node;
import kasuga.lib.core.client.gui.style.PixelUnit;
import kasuga.lib.core.client.gui.style.Style;
import kasuga.lib.core.client.gui.style.StyleType;
import kasuga.lib.core.util.data_type.Pair;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class SizeStyle extends Style<Pair<Float, PixelUnit>> {

    public final String original;
    public final Pair<Float,PixelUnit> value;

    public SizeStyle(float value,PixelUnit unit){
        this.value = Pair.of(value,unit);
        this.original = unit.toString(value);
    }

    public SizeStyle(String source){
        this.original = source;
        this.value = PixelUnit.parse(source);
    }

    @Override
    public boolean isValid(Map<StyleType<?>, Style<?>> origin) {
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

    public static SizeStyleType createType(BiConsumer<Node, Pair<Float, PixelUnit>> consumer){
        return new SizeStyleType(consumer);
    }

    public static class SizeStyleType implements StyleType<SizeStyle>{


        private final BiConsumer<Node, Pair<Float, PixelUnit>> consumer;

        SizeStyleType(BiConsumer<Node,Pair<Float, PixelUnit>> consumer){
            this.consumer = consumer;
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
                public StyleType<?> getType() {
                    return type;
                }

                @Override
                public void apply(Node node) {
                    consumer.accept(node,value);
                }
            };
        }

        public SizeStyle create(float value, PixelUnit unit){
            SizeStyle.SizeStyleType type = this;
            return new SizeStyle(value,unit) {
                @Override
                public StyleType<?> getType() {
                    return type;
                }

                @Override
                public void apply(Node node) {
                    consumer.accept(node,value);
                    node.markReLayout();
                }
            };
        }
    }
}
