package kasuga.lib.core.util.nbt_json.primitive;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

public class StringPair extends PrimitivePair<StringTag, String> {

    public StringPair() {
        super("str", StringTag.class, String.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        return primitive.isString();
    }

    @Override
    public JsonPrimitive convert(@NotNull StringTag nbt, String path) {
        return new JsonPrimitive(nbt.toString());
    }

    @Override
    public StringTag convert(@NotNull JsonPrimitive json, String path) {
        return StringTag.valueOf(json.getAsString());
    }
}
