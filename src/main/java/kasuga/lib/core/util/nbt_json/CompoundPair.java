package kasuga.lib.core.util.nbt_json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Getter
public class CompoundPair extends ConversionPair<CompoundTag, JsonObject> {

    public final Converter converter;
    
    public CompoundPair(Converter converter) {
        super("compound", CompoundTag.class, JsonObject.class);
        this.converter = converter;
    }

    @Override
    public JsonObject convert(@NotNull CompoundTag nbt, String path) throws NoAvailableConversionException {
        JsonObject object = new JsonObject();
        for (String key : nbt.getAllKeys()) {
            Tag tag = nbt.get(key);
            object.add(key, (JsonElement) converter.innerConvert(tag, path + "." + key));
        }
        return object;
    }

    @Override
    public CompoundTag convert(@NotNull JsonObject json, String path) throws NoAvailableConversionException {
        CompoundTag nbt = new CompoundTag();
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            nbt.put(entry.getKey(), (Tag) converter.innerConvert(entry.getValue(),
                    path + "." + entry.getKey()));
        }
        return nbt;
    }
}
