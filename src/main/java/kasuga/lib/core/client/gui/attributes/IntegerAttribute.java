package kasuga.lib.core.client.gui.attributes;

import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import kasuga.lib.core.client.gui.SimpleWidget;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class IntegerAttribute extends Attribute<Integer> {
    int value = 0;

    IntegerAttribute(int value){
        this.value = value;
    }

    IntegerAttribute(String value){
        try{
            this.value = Integer.parseInt(value);
        }catch (NumberFormatException e){
            this.value = 0;
        }
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static AttributeType<Integer> of(NonNullBiConsumer<SimpleWidget,Integer> consumer){
        return of(consumer,(w,i)->true);
    }

    public static AttributeType<Integer> of(NonNullBiConsumer<SimpleWidget,Integer> consumer, BiFunction<SimpleWidget,Integer,Boolean> filter){
        return AttributeType.of((val)->new IntegerAttribute(val) {
            @Override
            public void apply(SimpleWidget widget) {
                consumer.accept(widget,value);
            }

            @Override
            public boolean canApplyTo(SimpleWidget widget) {
                return filter.apply(widget,value);
            }
        },(val)->new IntegerAttribute(val) {
            @Override
            public void apply(SimpleWidget widget) {
                consumer.accept(widget,value);
            }

            @Override
            public boolean canApplyTo(SimpleWidget widget) {
                return filter.apply(widget,value);
            }
        });
    }
}
