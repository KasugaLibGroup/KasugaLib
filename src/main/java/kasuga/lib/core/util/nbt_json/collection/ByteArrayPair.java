package kasuga.lib.core.util.nbt_json.collection;

import com.google.gson.JsonArray;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import org.jetbrains.annotations.NotNull;

public class ByteArrayPair extends CollectionPair<ByteTag, ByteArrayTag> {


    public ByteArrayPair() {
        super("byte_array", ByteArrayTag.class, ByteTag.class);
    }

    @Override
    public JsonArray convert(@NotNull ByteArrayTag nbt, String path) {
        JsonArray array = new JsonArray();
        nbt.forEach(content -> array.add(content.getAsByte()));
        return array;
    }

    @Override
    public ByteArrayTag convert(@NotNull JsonArray json, String path) {
        byte[] result = new byte[json.size()];
        for (int i = 0; i < json.size(); i++) {
            result[i] = json.get(i).getAsByte();
        }
        return new ByteArrayTag(result);
    }
}
