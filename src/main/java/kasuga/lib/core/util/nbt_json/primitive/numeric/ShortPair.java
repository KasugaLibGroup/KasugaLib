package kasuga.lib.core.util.nbt_json.primitive.numeric;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.ShortTag;
import org.jetbrains.annotations.NotNull;

public class ShortPair extends NumericPair<ShortTag, Short> {

    public ShortPair() {
        super("short", ShortTag.class, Short.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        String str = element.getAsString();
        Number number = element.getAsNumber();
        return !str.contains(".") &&
                number.intValue() < 32768 &&
                number.intValue() > -32769;
    }

    @Override
    public JsonPrimitive convert(@NotNull ShortTag nbt, String path) {
        return new JsonPrimitive(nbt.getAsShort());
    }

    @Override
    public ShortTag convert(@NotNull JsonPrimitive json, String path) {
        return ShortTag.valueOf(json.getAsShort());
    }
}
