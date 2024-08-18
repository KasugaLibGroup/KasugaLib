package kasuga.lib.core.client.animation.neo_neo.scaling;

import com.mojang.blaze3d.vertex.PoseStack;
import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.ICustom;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import static interpreter.Code.ROOT_NAMESPACE;

public class CustomScaling extends Scaling implements ICustom {
    private final Namespace namespace;

    Formula x, y, z;
    private Pair<Float, Vec3> cache;
    public CustomScaling(Namespace namespace, String x, String y, String z, float startTime, float endTime) {
        super(STARTER, startTime, endTime);
        this.namespace = namespace;
        this.x = namespace.decodeFormula(x);
        this.y = namespace.decodeFormula(y);
        this.z = namespace.decodeFormula(z);
        this.cache = Pair.of(0f, STARTER);
        namespace.assign("start_time", startTime);
        namespace.assign("end_time", endTime);
    }

    public CustomScaling(String x, String y, String z, float startTime, float endTime) {
        this(new Namespace(ROOT_NAMESPACE), x, y, z, startTime, endTime);
    }

    public CustomScaling(Namespace namespace, float startTime, float endTime) {
        this(namespace, "0", "0", "0", startTime, endTime);
    }

    public CustomScaling(float startTime, float endTime) {
        this(new Namespace(ROOT_NAMESPACE), startTime, endTime);
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
        this.z = namespace.decodeFormula(z);
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

    @Override
    public Namespace getNamespace() {
        return namespace;
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

    @Override
    public Vec3 getPercentage(float percentage) {
        if (cache.getFirst().equals(percentage))
            return cache.getSecond();
        namespace.assign("t", percentage);
        Vec3 result = new Vec3(x.getResult(), y.getResult(), z.getResult());
        result = STARTER.add(STARTER.subtract(result));
        cache = Pair.of(percentage, result);
        return result;
    }

    @Override
    public void move(float time, PoseStack pose) {
        VectorUtil.scale(pose, getPercentage(time));
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        readCustomFormulas(nbt, "custom");
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        writeCustomFormulas(nbt, "custom");
    }
}
