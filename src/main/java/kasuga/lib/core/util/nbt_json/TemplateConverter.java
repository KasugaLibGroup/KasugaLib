package kasuga.lib.core.util.nbt_json;

import kasuga.lib.core.util.nbt_json.collection.ListPair;
import lombok.Getter;
import net.minecraft.nbt.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Getter
public class TemplateConverter extends Converter {

    private final Tag template;

    private final HashSet<String> boolTags;

    public TemplateConverter(Tag template) {
        this.template = template;
        boolTags = new HashSet<>();
    }

    public void markAsBoolTag(String... path) {
        boolTags.addAll(List.of(path));
    }

    public boolean isMarkedAsBoolTag(String path) {
        return boolTags.contains(path);
    }

    public void compile() throws ListIsEmptyException, NoAvailableConversionException {
        updateSelector(template, "root");
    }

    public boolean safeCompile() {
        return safeUpdateSelector(template, "root");
    }

    private void updateSelector(Tag tag, String path)
            throws ListIsEmptyException, NoAvailableConversionException {
        if (tag instanceof CompoundTag) {
            updateCompoundSelector((CompoundTag) tag, path);
            return;
        }
        ConversionPair pair = getPair(tag, path);
        addPathSelector(path, (self, nbt, json, path1, direction) -> pair);
    }

    private boolean safeUpdateSelector(Tag tag, String path) {
        try {
            updateSelector(tag, path);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // Kuayue.LOGGER.debug("Failed to update nbt template selector", e);
            return false;
        }
    }

    private void updateCompoundSelector(CompoundTag nbt, String path)
            throws ListIsEmptyException, NoAvailableConversionException {
        for (String key : nbt.getAllKeys()) {
            updateSelector(nbt.get(key), path + "." + key);
        }
    }

    private ConversionPair getPair(Tag tag, String path)
            throws ListIsEmptyException, NoAvailableConversionException {
        ConversionPair result = null;
        result = getPrimitivePair(tag, path);
        if (result != null) return result;
        result = getCollectionPair(tag, path);
        if (result != null) return result;
        throw new NoAvailableConversionException(tag.getClass(), path);
    }

    private ConversionPair getPrimitivePair(Tag tag, String path) {
        if (tag instanceof StringTag) {return getPair("str");}
        if (tag instanceof IntTag) {return getPair("int");}
        if (tag instanceof LongTag) {return getPair("long");}
        if (tag instanceof DoubleTag) {return getPair("double");}
        if (tag instanceof FloatTag) {return getPair("float");}
        if (tag instanceof ShortTag) {return getPair("short");}
        if (tag instanceof ByteTag) {
            if (isMarkedAsBoolTag(path)) return getPair("bool");
            return getPair("byte");
        }
        return null;
    }

    private ConversionPair getCollectionPair(Tag tag, String path)
            throws ListIsEmptyException, NoAvailableConversionException {
        if (tag instanceof IntArrayTag) {return getPair("int_array");}
        if (tag instanceof LongArrayTag) {return getPair("long_array");}
        if (tag instanceof ByteArrayTag) {return getPair("byte_array");}
        if (tag instanceof ListTag lTag) {
            if (lTag.isEmpty()) {
                throw new ListIsEmptyException(tag, path);
            }
            Tag t = lTag.get(0);
            ConversionPair pair = getPair(t, path);
            return new ListPair(pair);
        }
        return null;
    }
}
