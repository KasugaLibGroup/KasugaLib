package kasuga.lib.core.util.nbt_json.primitive.numeric;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.LongTag;
import org.jetbrains.annotations.NotNull;

public class LongPair extends NumericPair<LongTag, Long> {

    public LongPair() {
        super("long", LongTag.class, Long.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        String str = element.getAsString();
        Number number = element.getAsNumber();
        return !str.contains(".") && number.longValue() != number.intValue();
    }

    @Override
    public JsonPrimitive convert(@NotNull LongTag nbt, String path) {
        return new JsonPrimitive(nbt.getAsLong());
    }

    @Override
    public LongTag convert(@NotNull JsonPrimitive json, String path) {
        return LongTag.valueOf(json.getAsLong());
    }
}
