package kasuga.lib.core.javascript;

import kasuga.lib.core.javascript.engine.HostAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.List;

public class CompoundTagWrapper {
    CompoundTag tag;

    public CompoundTagWrapper(CompoundTag tag) {
        this.tag = tag;
    }

    @HostAccess.Export
    public String getType(String key) {
        return switch (tag.get(key).getId()) {
            case Tag.TAG_BYTE -> "byte";
            case Tag.TAG_SHORT -> "short";
            case Tag.TAG_INT -> "int";
            case Tag.TAG_LONG -> "long";
            case Tag.TAG_FLOAT -> "float";
            case Tag.TAG_DOUBLE -> "double";
            case Tag.TAG_BYTE_ARRAY -> "byteArray";
            case Tag.TAG_STRING -> "string";
            case Tag.TAG_LIST -> "list";
            case Tag.TAG_COMPOUND -> "compound";
            case Tag.TAG_INT_ARRAY -> "intArray";
            case Tag.TAG_LONG_ARRAY -> "longArray";
            case Tag.TAG_ANY_NUMERIC -> "numeric";
            case Tag.TAG_END -> "end";
            default -> throw new IllegalStateException("Unexpected value: " + tag.get(key).getId());
        };
    }

    @HostAccess.Export
    public byte getByte(String key) {
        return tag.getByte(key);
    }

    @HostAccess.Export
    public short getShort(String key) {
        return tag.getShort(key);
    }

    @HostAccess.Export
    public int getInt(String key) {
        return tag.getInt(key);
    }

    @HostAccess.Export
    public long getLong(String key) {
        return tag.getLong(key);
    }

    @HostAccess.Export
    public float getFloat(String key) {
        return tag.getFloat(key);
    }

    @HostAccess.Export
    public double getDouble(String key) {
        return tag.getDouble(key);
    }

    @HostAccess.Export
    public byte[] getByteArray(String key) {
        return tag.getByteArray(key);
    }

    @HostAccess.Export
    public String getString(String key) {
        return tag.getString(key);
    }

    @HostAccess.Export
    public ListTag getList(String key, int pTagType) {
        return tag.getList(key, pTagType);
    }

    @HostAccess.Export
    public CompoundTag getCompound(String key) {
        return tag.getCompound(key);
    }

    @HostAccess.Export
    public int[] getIntArray(String key) {
        return tag.getIntArray(key);
    }

    @HostAccess.Export
    public long[] getLongArray(String key) {
        return tag.getLongArray(key);
    }

    @HostAccess.Export
    public void putByte(String key, byte value) {
        tag.putByte(key, value);
    }

    @HostAccess.Export
    public void putShort(String key, short value) {
        tag.putShort(key, value);
    }

    @HostAccess.Export
    public void putInt(String key, int value) {
        tag.putInt(key, value);
    }

    @HostAccess.Export
    public void putLong(String key, long value) {
        tag.putLong(key, value);
    }

    @HostAccess.Export
    public void putFloat(String key, float value) {
        tag.putFloat(key, value);
    }

    @HostAccess.Export
    public void putDouble(String key, double value) {
        tag.putDouble(key, value);
    }

    @HostAccess.Export
    public void putByteArray(String key, byte[] value) {
        tag.putByteArray(key, value);
    }

    @HostAccess.Export
    public void putString(String key, String value) {
        tag.putString(key, value);
    }

    @HostAccess.Export
    public void putList(String key, ListTag value) {
        tag.put(key, value);
    }

    @HostAccess.Export
    public void putCompound(String key, CompoundTag value) {
        tag.put(key, value);
    }

    @HostAccess.Export
    public void putIntArray(String key, int[] value) {
        tag.putIntArray(key, value);
    }

    @HostAccess.Export
    public void putLongArray(String key, long[] value) {
        tag.putLongArray(key, value);
    }
}
