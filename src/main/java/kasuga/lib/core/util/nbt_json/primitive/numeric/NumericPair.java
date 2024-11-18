package kasuga.lib.core.util.nbt_json.primitive.numeric;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import kasuga.lib.core.util.nbt_json.primitive.PrimitivePair;
import net.minecraft.nbt.NumericTag;

public abstract class NumericPair<T extends NumericTag, K extends Number> extends PrimitivePair<T, K> {

    public NumericPair(String identifier, Class<T> tagClazz, Class<K> primitiveClazz) {
        super(identifier, tagClazz, primitiveClazz);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        return primitive.isNumber();
    }
}
