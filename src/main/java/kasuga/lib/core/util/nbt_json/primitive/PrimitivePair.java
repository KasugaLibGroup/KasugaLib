package kasuga.lib.core.util.nbt_json.primitive;

import com.google.gson.JsonPrimitive;
import kasuga.lib.core.util.nbt_json.ConversionPair;
import lombok.Getter;
import net.minecraft.nbt.Tag;

@Getter
public abstract class PrimitivePair<T extends Tag, Q> extends ConversionPair<T, JsonPrimitive> {

    public final Class<Q> primitiveType;

    public PrimitivePair(String identifier, Class<T> tagClazz, Class<Q> primitiveClazz) {
        super(identifier, tagClazz, JsonPrimitive.class);
        this.primitiveType = primitiveClazz;
    }
}
