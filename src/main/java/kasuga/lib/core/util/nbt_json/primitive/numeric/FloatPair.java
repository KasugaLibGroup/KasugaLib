package kasuga.lib.core.util.nbt_json.primitive.numeric;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.FloatTag;
import org.jetbrains.annotations.NotNull;

public class FloatPair extends NumericPair<FloatTag, Float> {

    public FloatPair() {
        super("float", FloatTag.class, Float.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        String str = element.getAsString();
        return str.contains(".");
    }

    @Override
    public JsonPrimitive convert(@NotNull FloatTag nbt, String path) {
        return new JsonPrimitive(nbt.getAsFloat());
    }

    @Override
    public FloatTag convert(@NotNull JsonPrimitive json, String path) {
        return FloatTag.valueOf(json.getAsFloat());
    }
}