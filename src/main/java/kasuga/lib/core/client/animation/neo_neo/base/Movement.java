package kasuga.lib.core.client.animation.neo_neo.base;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.base.NbtSerializable;
import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.point.PivotPoint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public abstract class Movement implements NbtSerializable {

    protected float startTime, endTime;
    protected Vec3 data;

    public Movement(Vec3 data, float startTime, float endTime) {
        this.data = data;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public void setData(Vec3 data) {
        this.data = data;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public Vec3 getData() {
        return data;
    }

    public float getStartTime() {
        return startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    public float getTime() {
        return endTime - startTime;
    }

    public float calculateTime(float time) {
        return (time - startTime) / getTime();
    }

    public abstract Vec3 getPercentage(float percentage);

    public abstract void move(float time, PoseStack pose);

    public void reset() {
        this.data = Vec3.ZERO;
    }

    public abstract void apply(PivotPoint point, float time);

    public void write(CompoundTag nbt) {
        VectorIOUtil.writeVec3ToNbt(nbt, "data", data);
        nbt.putFloat("start_time", startTime);
        nbt.putFloat("end_time", endTime);
    }

    public void read(CompoundTag nbt) {
        this.setData(VectorIOUtil.getVec3FromNbt(nbt, "data"));
        this.setStartTime(nbt.getFloat("start_time"));
        this.setEndTime(nbt.getFloat("end_time"));
    }

    public void writeJson(JsonObject json) {

    }

    public void readJson(JsonObject json) {

    }

    public void readAdditionalData(CompoundTag nbt) {}

    public void writeAdditionalData(CompoundTag nbt) {}
}
