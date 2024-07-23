package kasuga.lib.core.client.render.texture;

import kasuga.lib.core.annos.Util;
import kasuga.lib.core.util.data_type.Vec2i;
import net.minecraft.nbt.CompoundTag;

public class Vec2f {
    private float x, y;

    public static final Vec2f ZERO = new Vec2f(0f, 0f);
    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vec2f(Vec2f from) {
        this.x = from.x;
        this.y = from.y;
    }

    public Vec2f(Vec2i vec2i) {
        this.x = vec2i.x;
        this.y = vec2i.y;
    }

    public Vec2f(CompoundTag nbt) {
        this.x = nbt.getFloat("x");
        this.y = nbt.getFloat("y");
    }

    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("x", x);
        nbt.putFloat("y", y);
        return nbt;
    }

    public Vec2f copy() {
        return new Vec2f(this);
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public Vec2f set(float x, float y) {
        return setX(x).setY(y);
    }

    public float[] get() {
        return new float[]{x, y};
    }

    public Vec2f setX(float x) {
        this.x = x;
        return this;
    }

    public Vec2f setY(float y) {
        this.y = y;
        return this;
    }

    public Vec2f add(float x, float y) {
        return new Vec2f(this.x + x, this.y + y);
    }

    public Vec2f add(Vec2f vec2f) {
        return new Vec2f(this.x + vec2f.x, this.y + vec2f.y);
    }

    public Vec2f subtract(float x, float y) {
        return new Vec2f(this.x - x, this.y - y);
    }

    public Vec2f subtract(Vec2f vec2f) {
        return new Vec2f(this.x - vec2f.x, this.y - vec2f.y);
    }

    public Vec2f scale(float x, float y) {
        return new Vec2f(this.x * x, this.y * y);
    }

    public Vec2f scale(float factor) {
        return scale(factor, factor);
    }

    public Vec2f scale(Vec2f vec2f) {
        return new Vec2f(this.x * vec2f.x, this.y * vec2f.y);
    }

    public float dot(Vec2f vec2f) {
        return x * vec2f.x + y * vec2f.y;
    }

    public float cross(Vec2i vec2i) {
        return x * vec2i.y - vec2i.x * y;
    }

    public Vec2f normal() {
        return scale(1 / length());
    }

    public boolean isPerpendicular(Vec2f vec2f) {
        return dot(vec2f) == 0f;
    }

    public boolean isParallel(Vec2f vec2f) {
        if (x == 0) {
            if (y == 0) {
                return vec2f.x == x && vec2f.y == y;
            }
            return vec2f.x == x;
        }
        if (y == 0) {
            return vec2f.y == y;
        }
        return vec2f.x / x == vec2f.y / y;
    }

    public Vec2f invert() {
        return new Vec2f(-x, -y);
    }

    public Vec2f rotateDeg(Vec2f pivot, float deg) {
        return rotate(pivot, deg / 180f * (float) Math.PI);
    }

    public Vec2f rotateDeg(float deg) {
        return rotateDeg(ZERO, deg);
    }

    public Vec2f rotate(Vec2f pivot, float rad) {
        Vec2f offset = subtract(pivot);
        float x0 = offset.x(), y0 = offset.y();
        offset.setX((float) (x0 * Math.cos(rad) - y0 * Math.sin(rad)));
        offset.setY((float) (y0 * Math.cos(rad) - x0 * Math.sin(rad)));
        return pivot.add(offset);
    }

    public Vec2f rotate(float rad) {
        return rotate(ZERO, rad);
    }

    public float lengthSqr() {
        return x * x + y * y;
    }

    public float length() {
        return (float) Math.sqrt(lengthSqr());
    }

    public float distanceSqr(Vec2f vec2f) {
        return subtract(vec2f).lengthSqr();
    }

    public float distance(Vec2f vec2f) {
        return (float) Math.sqrt(distanceSqr(vec2f));
    }

    @Util
    public static Vec2f average(Vec2f... vectors) {
        if (vectors.length < 1) return Vec2f.ZERO;
        if (vectors.length == 1) return vectors[0];
        Vec2f vec = Vec2f.ZERO;
        for (Vec2f v : vectors) vec = vec.add(v);
        return vec.scale(1 / (float) vectors.length);
    }
}
