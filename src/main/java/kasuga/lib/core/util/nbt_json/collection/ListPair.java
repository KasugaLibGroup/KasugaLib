package kasuga.lib.core.util.nbt_json.collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import kasuga.lib.core.util.nbt_json.ConversionPair;
import kasuga.lib.core.util.nbt_json.NoAvailableConversionException;
import lombok.Getter;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

@Getter
public class ListPair extends CollectionPair<Tag, ListTag> {

    private final ConversionPair pair;

    public ListPair(ConversionPair pair) {
        super("list", ListTag.class, Tag.class);
        this.pair = pair;
    }

    @Override
    public boolean match(Tag tag) {
        if (!super.match(tag)) return false;
        ListTag list = (ListTag) tag;
        for (Tag t : list) {
            if (!pair.match(t)) return false;
        }
        return true;
    }

    public boolean match(JsonElement element) {
        if (!super.match(element)) return false;
        JsonArray array = element.getAsJsonArray();
        for (JsonElement e : array) {
            if (!pair.match(e)) return false;
        }
        return true;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public JsonArray convert(@NotNull ListTag nbt, String path) throws NoAvailableConversionException {
        JsonArray array = new JsonArray();
        for (Tag content : nbt) {
            array.add(pair.convert(content, path));
        }
        return array;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public ListTag convert(@NotNull JsonArray json, String path) throws NoAvailableConversionException {
        ListTag listTag = new ListTag();
        for (JsonElement e : json) {
            listTag.add(pair.convert(e, path));
        }
        return listTag;
    }
}
