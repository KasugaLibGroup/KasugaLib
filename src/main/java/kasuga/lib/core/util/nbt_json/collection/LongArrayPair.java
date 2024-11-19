package kasuga.lib.core.util.nbt_json.collection;

import com.google.gson.JsonArray;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import org.jetbrains.annotations.NotNull;

public class LongArrayPair extends CollectionPair<LongTag, LongArrayTag> {

    public LongArrayPair() {
        super("long_array", LongArrayTag.class, LongTag.class);
    }

    @Override
    public JsonArray convert(@NotNull LongArrayTag nbt, String path) {
        JsonArray array = new JsonArray();
        nbt.forEach(content -> array.add(content.getAsLong()));
        return array;
    }

    @Override
    public LongArrayTag convert(@NotNull JsonArray json, String path) {
        long[] array = new long[json.size()];
        for (int i = 0; i < array.length; ++i)
            array[i] = json.get(i).getAsLong();
        return new LongArrayTag(array);
    }
}
