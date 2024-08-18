package kasuga.lib.core.client.animation.neo_neo.point;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.base.NbtSerializable;
import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PivotPoint implements NbtSerializable {
    private Vec3 position, rotation;
    private Quaternionf cacheQ;
    boolean degree;
    public PivotPoint(Vec3 position, Vec3 rotation, boolean isDegree) {
        this.position = position;
        this.rotation = rotation;
        this.degree = isDegree;
        cacheQ = getQuaternion();
    }

    public PivotPoint(boolean isDegree) {
        this.position = Vec3.ZERO;
        this.rotation = Vec3.ZERO;
        this.degree = isDegree;
    }

    public PivotPoint() {
        this(true);
    }

    public Vec3 getPosition() {
        return this.position;
    }

    public Vec3 getRotation() {
        return this.rotation;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public void setRotation(Vec3 rotation) {
        this.rotation = rotation;
    }

    public void absTranslate(Vec3 offset) {
        this.position.add(offset);
    }

    public void absTranslate(double x, double y, double z) {
        this.position.add(x ,y, z);
    }

    public void translate(Vec3 offset) {
        Quaternionf quaternion = cacheQ;
        Vector3f vector3f = VectorUtil.vec3ToVec3f(offset);
        vector3f.rotate(quaternion);
        absTranslate(vector3f.x(), vector3f.y(), vector3f.z());
    }

    public void translate(double x, double y, double z) {
        translate(new Vec3(x, y, z));
    }

    public Quaternionf getQuaternion() {
        return VectorUtil.getQuaternionf(this.rotation, degree);
    }

    public boolean isDegree() {
        return degree;
    }
    public void setDegree(boolean isDegree) {
        if (this.degree == isDegree) return;
        this.rotation = VectorUtil.translateDegAndRad(this.rotation, isDegree);
    }

    public void rotate(Vec3 rotation, boolean isDegree) {
        Vec3 rot = degree != isDegree ? VectorUtil.translateDegAndRad(rotation, this.degree) : rotation;
        this.rotation = VectorUtil.rot(this.rotation, rot, this.degree);
        this.cacheQ = getQuaternion();
    }

    public PivotPoint mul(PivotPoint another) {
        Vec3 pos = this.position.add(another.position);
        Vec3 r = this.degree != another.degree ? VectorUtil.translateDegAndRad(another.rotation, this.degree) : another.rotation;
        Vec3 rot = VectorUtil.rot(this.rotation, r, this.degree);
        return new PivotPoint(pos, rot, this.degree);
    }

    public void apply(PoseStack pose) {
        VectorUtil.translate(pose, position);
        VectorUtil.rot(pose, rotation, degree);
    }

    public void reverseApply(PoseStack pose) {
        VectorUtil.rot(pose, rotation.reverse(), degree);
        VectorUtil.translate(pose, position.reverse());
    }

    public void write(CompoundTag nbt) {
        VectorIOUtil.writeVec3ToNbt(nbt, "pos", this.position);
        VectorIOUtil.writeVec3ToNbt(nbt, "rot", this.rotation);
        nbt.putBoolean("degree", degree);
    }

    public void read(CompoundTag nbt) {
        this.position = VectorIOUtil.getVec3FromNbt(nbt, "pos");
        this.rotation = VectorIOUtil.getVec3FromNbt(nbt, "rot");
        this.degree = nbt.getBoolean("degree");
    }
}
