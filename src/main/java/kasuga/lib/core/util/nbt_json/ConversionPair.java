package kasuga.lib.core.util.nbt_json;

import com.google.gson.JsonElement;
import lombok.Getter;
import net.minecraft.nbt.Tag;
import org.checkerframework.checker.nullness.qual.NonNull;

@Getter
public abstract class ConversionPair<T extends Tag, K extends JsonElement> {

    public final Class<T> tagClazz;
    public final Class<K> jsonClazz;
    public final String identifier;

    public ConversionPair(String identifier, Class<T> tagClazz, Class<K> jsonClazz) {
        this.tagClazz = tagClazz;
        this.jsonClazz = jsonClazz;
        this.identifier = identifier;
    }

    public boolean match(Tag tag) {
        return tagClazz.isInstance(tag);
    }

    public boolean match(JsonElement element) {
        return jsonClazz.isInstance(element);
    }

    public abstract K convert(@NonNull T nbt, String path) throws NoAvailableConversionException;

    public abstract T convert(@NonNull K json, String path) throws NoAvailableConversionException;
}
