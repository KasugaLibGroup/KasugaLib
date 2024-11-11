package kasuga.lib.core.util.nbt_json.primitive.numeric;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.IntTag;
import org.jetbrains.annotations.NotNull;

public class IntPair extends NumericPair<IntTag, Integer> {

    public IntPair() {
        super("int", IntTag.class, Integer.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        String str = element.getAsString();
        Number number = element.getAsNumber();
        return !str.contains(".") && number.longValue() == number.intValue();
    }

    @Override
    public JsonPrimitive convert(@NotNull IntTag nbt, String path) {
        return new JsonPrimitive(nbt.getAsInt());
    }

    @Override
    public IntTag convert(@NotNull JsonPrimitive json, String path) {
        return IntTag.valueOf(json.getAsInt());
    }
}
