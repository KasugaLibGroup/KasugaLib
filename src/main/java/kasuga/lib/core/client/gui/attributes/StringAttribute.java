package kasuga.lib.core.client.gui.attributes;

import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import kasuga.lib.core.client.gui.SimpleWidget;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class StringAttribute extends Attribute<String> {
    String value = "";

    StringAttribute(String value){
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static AttributeType<String> of(NonNullBiConsumer<SimpleWidget,String> consumer){
        return of(consumer,(w,i)->true);
    }

    public static AttributeType<String> of(NonNullBiConsumer<SimpleWidget,String> consumer, BiFunction<SimpleWidget,String,Boolean> filter){
        NonNullFunction<String,Attribute<String>> constructor = (val)-> new StringAttribute(val){
            @Override
            public void apply(SimpleWidget widget) {
                consumer.accept(widget,value);
            }

            @Override
            public boolean canApplyTo(SimpleWidget widget) {
                return filter.apply(widget,value);
            }
        };
        return AttributeType.of(constructor,constructor);
    }
}
