package kasuga.lib.core.util.nbt_json.collection;

import com.google.gson.JsonArray;
import kasuga.lib.core.util.nbt_json.ConversionPair;
import lombok.Getter;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;

@Getter
public abstract class CollectionPair<C extends Tag, T extends CollectionTag<C>> extends ConversionPair<T, JsonArray> {

    public final Class<C> contentClazz;

    public CollectionPair(String identifier, Class<T> tagClazz, Class<C> contentClazz) {
        super(identifier, tagClazz, JsonArray.class);
        this.contentClazz = contentClazz;
    }
}
