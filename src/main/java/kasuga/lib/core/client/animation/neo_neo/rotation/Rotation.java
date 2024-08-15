package kasuga.lib.core.client.animation.neo_neo.rotation;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.client.animation.neo_neo.point.PivotPoint;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public abstract class Rotation extends Movement {
    private boolean degree;
    private PivotPoint pivot;
    public Rotation(Vec3 data, float startTime, float endTime, boolean degree) {
        super(data, startTime, endTime);
        this.degree = degree;
        pivot = new PivotPoint();
    }

    public boolean isDegree() {
        return degree;
    }

    public void setDegree(boolean degree) {
        if (degree == this.degree) return;
        this.data = VectorUtil.translateDegAndRad(this.data, degree);
        this.degree = degree;
    }

    public void rot(Vec3 vec3) {
        this.data = VectorUtil.rot(this.data, vec3, this.isDegree());
    }

    public PivotPoint getPivot() {
        return pivot;
    }

    public void offsetPivot(double x, double y, double z) {
        pivot.translate(x, y, z);
    }

    public void offsetPivot(Vec3 offset) {
        pivot.translate(offset);
    }

    public void absOffsetPivot(double x, double y, double z) {
        pivot.absTranslate(x, y, z);
    }

    public void absOffsetPivot(Vec3 offset) {
        pivot.absTranslate(offset);
    }

    public void rotPivot(Vec3 rot, boolean degree) {
        pivot.rotate(rot, degree);
    }

    public void resetPivot() {
        pivot.setPosition(Vec3.ZERO);
        pivot.setRotation(Vec3.ZERO);
    }

    public void setPivot(PivotPoint pivot) {
        this.pivot = pivot;
    }

    @Override
    public abstract Vec3 getPercentage(float percentage);

    @Override
    public void move(float time, PoseStack pose) {
        pivot.apply(pose);
        pose.mulPose(VectorUtil.getQuaternion(getPercentage(calculateTime(time)), degree));
        pivot.reverseApply(pose);
    }

    @Override
    public void apply(PivotPoint point, float time) {
        point.rotate(getPercentage(calculateTime(time)), this.isDegree());
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        nbt.putBoolean("degree", this.isDegree());
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        this.degree = nbt.getBoolean("degree");
    }
}
