package kasuga.lib.core.util.nbt_json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import kasuga.lib.KasugaLib;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static kasuga.lib.core.util.nbt_json.AllConversionPairs.*;

@Getter
public class Converter {

    private final HashMap<String, ConversionPair> pairs;

    @Setter
    private ConverterSelector globalSelector;

    private final HashMap<String, ConverterSelector> pathSelectors;

    private final HashMap<ConversionPair, ConverterSelector> selectors;

    public Converter() {
        pairs = new HashMap<>();
        selectors = new HashMap<>();
        pathSelectors = new HashMap<>();
    }

    public Converter(final ConversionPair... pairs) {
        this();
        for (ConversionPair pair : pairs) this.pairs.put(pair.identifier, pair);
    }

    public void addDefaultConversions() {
        this.addConversion(BOOLEAN_PAIR);
        this.addConversion(STRING_PAIR);
        this.addConversion(BYTE_PAIR);
        this.addConversion(DOUBLE_PAIR);
        this.addConversion(FLOAT_PAIR);
        this.addConversion(INT_PAIR);
        this.addConversion(LONG_PAIR);
        this.addConversion(SHORT_PAIR);
        this.addConversion(BYTE_ARRAY_PAIR);
        this.addConversion(INT_ARRAY_PAIR);
        this.addConversion(LONG_ARRAY_PAIR);
        this.addConversion(new CompoundPair(this));
    }

    public void markAs(String path, String identifier) {
        pathSelectors.put(path, (a, b, c, d, e) -> pairs.get(identifier));
    }

    public void addConversion(final ConversionPair pair) {
        this.pairs.put(pair.identifier, pair);
    }

    public void addSelector(final ConversionPair pair, final ConverterSelector selector) {
        selectors.put(pair, selector);
    }

    public void addPathSelector(final String path, final ConverterSelector selector) {
        pathSelectors.put(path, selector);
    }

    public ConversionPair getPair(final String identifier) {
        return pairs.getOrDefault(identifier, null);
    }

    public ConverterSelector getSelector(final ConversionPair pair) {
        return selectors.getOrDefault(pair, null);
    }

    public boolean hasGlobalSelector() {
        return globalSelector != null;
    }

    public @NonNull JsonElement convert(@NonNull Tag tag) throws NoAvailableConversionException {
        return (JsonElement) innerConvert(tag, "root");
    }

    public @NonNull Tag convert(@NonNull JsonElement element) throws NoAvailableConversionException {
        return (Tag) innerConvert(element, "root");
    }

    public @NonNull JsonElement safeConvert(@NonNull Tag tag) {
        return (JsonElement) safeInnerConvert(tag, "root");
    }

    public @NonNull Tag safeConvert(@NonNull JsonElement element) {
        return (Tag) safeInnerConvert(element, "root");
    }

    protected @NonNull Object safeInnerConvert(Object input, String path) {
        try {
            return innerConvert(input, path);
        } catch (NoAvailableConversionException e) {
            KasugaLib.MAIN_LOGGER.debug("Error in nbt convert: ", e);
            if (input instanceof Tag) return new JsonObject();
            return new CompoundTag();
        }
    }

    @SuppressWarnings("unchecked")
    protected @NonNull Object innerConvert(Object input, String path) throws NoAvailableConversionException {
        boolean flag = input instanceof Tag;
        Direction direction = flag ? Direction.NBT_TO_JSON : Direction.JSON_TO_NBT;
        Tag tag = flag ? (Tag) input: null;
        JsonElement element = !flag ? (JsonElement) input : null;

        ConversionPair pair = null;
        for (Map.Entry<String, ConverterSelector> entry : pathSelectors.entrySet()) {
            if (entry.getKey().equals(path)) {
                ConversionPair cache = entry.getValue().select(this, tag, element, path, direction);
                if (flag ? cache.match(tag) : cache.match(element)) {
                    pair = cache;
                    break;
                }
            }
        }

        if (pair == null) {
            for (Map.Entry<ConversionPair, ConverterSelector> entry : selectors.entrySet()) {
                if (entry.getKey().match(tag)) {
                    pair = entry.getValue().select(this, tag, element, path, direction);
                    break;
                }
            }
        }

        if (globalSelector != null && pair == null) {
            pair = globalSelector.select(this, tag, element, path, direction);
        }

        if (pair == null) {
            if (flag || !(element.isJsonPrimitive() || element.isJsonArray())) {
                for (Map.Entry<String, ConversionPair> entry : pairs.entrySet()) {
                    if (flag ? entry.getValue().match(tag) : entry.getValue().match(element)) {
                        pair = entry.getValue();
                        break;
                    }
                }
            } else {
                if (element.isJsonPrimitive()) {
                    if (getPair("str").match(element))
                        pair = getPair("str");
                    else if (getPair("bool").match(element))
                        pair = getPair("bool");
                    else if (getPair("int").match(element))
                        pair = getPair("int");
                    else if (getPair("short").match(element))
                        pair = getPair("short");
                    else if (getPair("long").match(element))
                        pair = getPair("long");
                    else if (getPair("float").match(element))
                        pair = getPair("float");
                    else if (getPair("double").match(element))
                        pair = getPair("double");
                } else {
                    JsonArray array = new JsonArray();
                    if (array.isEmpty()) {
                        return new ListTag();
                    }
                    JsonElement element1 = array.get(0);
                    if (element1.isJsonPrimitive()) {
                        JsonPrimitive primitive = element1.getAsJsonPrimitive();
                        if (getPair("int").match(primitive))
                            pair = getPair("int_array");
                        else if (getPair("long").match(primitive))
                            pair = getPair("long_array");
                        else if (getPair("byte").match(primitive))
                            pair = getPair("byte_array");
                    } else {
                        ListTag listTag = new ListTag();
                        for (JsonElement inner : array) {
                            listTag.add((Tag) innerConvert(inner, path));
                        }
                        return listTag;
                    }
                }
            }
        }

        if (pair == null) throw new NoAvailableConversionException(tag.getClass(), path);
        return flag ? pair.convert(tag, path) : pair.convert(element, path);
    }

    public interface ConverterSelector {

        ConversionPair select(Converter self, @Nullable Tag nbt,
                              @Nullable JsonElement json, String path, Direction direction);
    }

    public enum Direction {
        JSON_TO_NBT,
        NBT_TO_JSON;
    }
}
