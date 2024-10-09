package kasuga.lib.core.client.animation.neo_neo.rotation;

import com.mojang.blaze3d.vertex.PoseStack;
import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.ICustom;
import kasuga.lib.core.client.animation.neo_neo.point.PivotPoint;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import static interpreter.Code.ROOT_NAMESPACE;

public class CustomRotation extends Rotation implements ICustom {
    Namespace namespace;
    Formula x, y, z;
    Pair<Float, Vec3> cache;
    public CustomRotation(Namespace namespace, String x, String y, String z, float startTime, float endTime, boolean degree) {
        super(Vec3.ZERO, startTime, endTime, degree);
        this.namespace = namespace;
        setFormulas(x, y, z);
        namespace.assign("start_time", startTime);
        namespace.assign("end_time", endTime);
    }

    public CustomRotation(String x, String y, String z, float startTime, float endTime, boolean degree) {
        this(new Namespace(ROOT_NAMESPACE), x, y, z, startTime, endTime, degree);
    }

    public CustomRotation(Namespace namespace, float startTime, float endTime, boolean degree) {
        this(namespace, "0", "0", "0", startTime, endTime, degree);
    }

    public CustomRotation(float startTime, float endTime, boolean degree) {
        this("0", "0", "0", startTime, endTime, degree);
    }

    @Override
    public Vec3 getPercentage(float percentage) {
        if (percentage == cache.getFirst())
            return cache.getSecond();
        namespace.assign("t", percentage);
        Vec3 result = new Vec3(x.getResult(), y.getResult(), z.getResult());
        cache = Pair.of(percentage, result);
        return result;
    }

    @Override
    public void move(float time, PoseStack pose) {
        getPivot().apply(pose);
        VectorUtil.rot(pose, getPercentage(time), this.isDegree());
        getPivot().reverseApply(pose);
    }

    @Override
    public void setX(String x) {
        this.x = namespace.decodeFormula(x);
    }

    @Override
    public void setY(String y) {
        this.y = namespace.decodeFormula(y);
    }

    @Override
    public void setZ(String z) {
        this.y = namespace.decodeFormula(z);
    }

    @Override
    public Formula getX() {
        return x;
    }

    @Override
    public Formula getY() {
        return y;
    }

    @Override
    public Formula getZ() {
        return z;
    }

    public void setFormulas(String x, String y, String z) {
        setX(x);
        setY(y);
        setZ(z);
        updateMovement();
    }

    @Override
    public void setStartTime(float startTime) {
        super.setStartTime(startTime);
        namespace.assign("start_time", startTime);
    }

    @Override
    public void setEndTime(float endTime) {
        super.setEndTime(endTime);
        namespace.assign("end_time", endTime);
    }

    public void reset() {
        x = namespace.decodeFormula("0");
        y = namespace.decodeFormula("0");
        z = namespace.decodeFormula("0");
        cache = Pair.of(0f, Vec3.ZERO);
        setData(Vec3.ZERO);
    }

    public void updateMovement() {
        float t = namespace.getInstance("t").getValue("t");
        namespace.assign("t", 1f);
        setData(new Vec3(x.getResult(), y.getResult(), z.getResult()));
        namespace.assign("t", t);
    }

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public void apply(PivotPoint point, float time) {
        point.rotate(getPercentage(time), this.isDegree());
    }

    public PivotPoint creatPivot() {
        return new PivotPoint(this.isDegree());
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        writeCustomFormulas(nbt, "custom");
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        readCustomFormulas(nbt, "custom");
    }
}
