package kasuga.lib.core.util.nbt_json.primitive.numeric;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

public class DoublePair extends NumericPair<DoubleTag, Double> {
    public DoublePair() {
        super("double", DoubleTag.class, Double.class);
    }

    @Override
    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        String str = element.getAsString();
        return str.contains(".");
    }

    @Override
    public JsonPrimitive convert(@NotNull DoubleTag nbt, String path) {
        return new JsonPrimitive(nbt.getAsDouble());
    }

    @Override
    public DoubleTag convert(@NotNull JsonPrimitive json, String path) {
        return DoubleTag.valueOf(json.getAsDouble());
    }
}
