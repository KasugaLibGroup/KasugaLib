package kasuga.lib.core.client.animation.neo_neo.point;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.scaling.Scaling;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

public class Point extends PivotPoint {
    private Vec3 scale;
    public Point(Vec3 position, Vec3 rotation, Vec3 scale, boolean isDegree) {
        super(position, rotation, isDegree);
        this.scale = scale;
    }

    public Point(boolean isDegree) {
        this(Vec3.ZERO, Vec3.ZERO, Scaling.STARTER, isDegree);
    }

    public Point() {
        this(true);
    }

    public Vec3 getScale() {
        return this.scale;
    }

    public void setScale(Vec3 scale) {
        this.scale = scale;
    }

    public void scale(Vec3 scale) {
        this.scale.multiply(scale);
    }

    @Override
    public PivotPoint mul(PivotPoint another) {
        if (another instanceof Point) {
            Vec3 pos = this.getPosition().add(another.getPosition());
            Vec3 r = this.degree != another.degree ? VectorUtil.translateDegAndRad(another.getPosition(), this.degree) : another.getPosition();
            Vec3 rot = VectorUtil.rot(this.getRotation(), r, this.degree);
            Vec3 scale = this.scale.multiply(((Point) another).scale);
            return new Point(pos, rot, scale, this.degree);
        }
        return super.mul(another);
    }

    @Override
    public void apply(PoseStack pose) {
        super.apply(pose);
        VectorUtil.scale(pose, scale);
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        this.scale = VectorIOUtil.getVec3FromNbt(nbt, "scale");
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        VectorIOUtil.writeVec3ToNbt(nbt, "scale", scale);
    }
}
