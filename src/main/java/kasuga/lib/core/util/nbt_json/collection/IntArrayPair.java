package kasuga.lib.core.util.nbt_json.collection;

import com.google.gson.JsonArray;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import org.jetbrains.annotations.NotNull;

public class IntArrayPair extends CollectionPair<IntTag, IntArrayTag> {

    public IntArrayPair() {
        super("int_array", IntArrayTag.class, IntTag.class);
    }

    @Override
    public JsonArray convert(@NotNull IntArrayTag nbt, String path) {
        JsonArray array = new JsonArray();
        nbt.forEach(content -> array.add(content.getAsInt()));
        return array;
    }

    @Override
    public IntArrayTag convert(@NotNull JsonArray json, String path) {
        int[] result = new int[json.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = json.get(i).getAsInt();
        }
        return new IntArrayTag(result);
    }
}
