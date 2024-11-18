package kasuga.lib.core.util.nbt_json;

import com.google.gson.JsonElement;
import kasuga.lib.core.util.nbt_json.collection.ByteArrayPair;
import kasuga.lib.core.util.nbt_json.collection.IntArrayPair;
import kasuga.lib.core.util.nbt_json.collection.LongArrayPair;
import kasuga.lib.core.util.nbt_json.primitive.BooleanPair;
import kasuga.lib.core.util.nbt_json.primitive.StringPair;
import kasuga.lib.core.util.nbt_json.primitive.numeric.*;
import net.minecraft.nbt.*;

public class AllConversionPairs {

    public static final BooleanPair BOOLEAN_PAIR = new BooleanPair();
    public static final StringPair STRING_PAIR = new StringPair();
    public static final BytePair BYTE_PAIR = new BytePair();
    public static final DoublePair DOUBLE_PAIR = new DoublePair();
    public static final FloatPair FLOAT_PAIR = new FloatPair();
    public static final IntPair INT_PAIR = new IntPair();
    public static final LongPair LONG_PAIR = new LongPair();
    public static final ShortPair SHORT_PAIR = new ShortPair();
    public static final ByteArrayPair BYTE_ARRAY_PAIR = new ByteArrayPair();
    public static final IntArrayPair INT_ARRAY_PAIR = new IntArrayPair();
    public static final LongArrayPair LONG_ARRAY_PAIR = new LongArrayPair();

    public static final Converter CONVERTER = new Converter();

    static {
        CONVERTER.addDefaultConversions();
    }
}
