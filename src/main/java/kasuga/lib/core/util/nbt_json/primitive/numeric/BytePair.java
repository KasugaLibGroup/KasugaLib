package kasuga.lib.core.util.nbt_json.primitive.numeric;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.ByteTag;
import org.jetbrains.annotations.NotNull;

public class BytePair extends NumericPair<ByteTag, Byte> {

    public BytePair() {
        super("byte", ByteTag.class, Byte.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        String str = element.getAsString();
        Number number = element.getAsNumber();
        return !str.contains(".") &&
                number.shortValue() > -129 &&
                number.shortValue() < 128;
    }

    @Override
    public JsonPrimitive convert(@NotNull ByteTag nbt, String path) {
        return new JsonPrimitive(nbt.getAsByte());
    }

    @Override
    public ByteTag convert(@NotNull JsonPrimitive json, String path) {
        return ByteTag.valueOf(json.getAsByte());
    }
}
