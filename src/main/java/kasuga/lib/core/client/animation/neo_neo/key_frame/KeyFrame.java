package kasuga.lib.core.client.animation.neo_neo.key_frame;

import kasuga.lib.core.base.NbtSerializable;
import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

public class KeyFrame<T extends Movement> implements NbtSerializable {
    private final MovementSupplier<T> supplier;
    private float time;
    private Vec3 data;
    private int color;
    private final CompoundTag nbt;
    T cache;
    public KeyFrame(MovementSupplier<T> supplier, Vec3 data, int color, float time) {
        this.supplier = supplier;
        this.color = color;
        this.data = data;
        this.time = time;
        nbt = new CompoundTag();
    }

    public KeyFrame(MovementSupplier<T> supplier, Vec3 data, float time) {
        this(supplier, data, 0xffffff, time);
    }

    public KeyFrame(MovementSupplier<T> supplier, Vec3 data) {
        this(supplier, data, -1);
    }

    public KeyFrame(MovementSupplier<T> supplier, int color) {
        this(supplier, Vec3.ZERO, color, -1);
    }

    public KeyFrame(MovementSupplier<T> supplier) {
        this(supplier, Vec3.ZERO);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float getTime() {
        return time;
    }

    public void setData(Vec3 data) {
        this.data = data;
    }

    public Vec3 getData() {
        return data;
    }

    public CompoundTag getNbt() {
        return nbt;
    }

    public MovementSupplier<T> getSupplier() {
        return supplier;
    }

    public T create(KeyFrame<?> next) {
        T result = supplier.get(this, next);
        this.cache = result;
        return result;
    }

    public KeyFrame<T> clone() {
        return new KeyFrame<T>(this.supplier, this.data, this.color);
    }

    public T getCache() {return cache;}



    public interface MovementSupplier<T> {
        T get(KeyFrame<?> current, KeyFrame<?> next);
    }

    public void write(CompoundTag nbt) {
        VectorIOUtil.writeVec3ToNbt(nbt, "data", data);
        nbt.putFloat("time", time);
        nbt.put("additional", this.nbt);
    }

    public void read(CompoundTag nbt) {
        this.data = VectorIOUtil.getVec3FromNbt(nbt, "data");
        this.time = nbt.getFloat("time");
        this.nbt.merge(nbt.getCompound("additional"));
    }
}
