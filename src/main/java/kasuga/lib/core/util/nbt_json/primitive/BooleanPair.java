package kasuga.lib.core.util.nbt_json.primitive;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.ByteTag;
import org.jetbrains.annotations.NotNull;

public class BooleanPair extends PrimitivePair<ByteTag, Boolean> {

    public BooleanPair() {
        super("bool", ByteTag.class, Boolean.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if(!super.match(element)) return false;
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        return primitive.isBoolean();
    }

    @Override
    public JsonPrimitive convert(@NotNull ByteTag nbt, String path) {
        return new JsonPrimitive(nbt.getAsByte() != 0);
    }

    @Override
    public ByteTag convert(@NotNull JsonPrimitive json, String path) {
        return ByteTag.valueOf(json.getAsBoolean());
    }
}
