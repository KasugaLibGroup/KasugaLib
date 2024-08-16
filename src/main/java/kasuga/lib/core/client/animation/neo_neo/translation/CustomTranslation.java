package kasuga.lib.core.client.animation.neo_neo.translation;

import com.mojang.blaze3d.vertex.PoseStack;
import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.ICustom;
import kasuga.lib.core.client.animation.neo_neo.InterpolationUtil;
import kasuga.lib.core.client.animation.neo_neo.point.PivotPoint;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import static interpreter.Code.ROOT_NAMESPACE;

public class CustomTranslation extends Translation implements ICustom {
    private Pair<Float, Vec3> cache;
    private final Namespace namespace;
    private Formula x, y, z;

    public CustomTranslation(Namespace namespace, String x, String y, String z, float startTime, float endTime) {
        super(Vec3.ZERO, startTime, endTime);
        this.namespace = namespace;
        this.x = namespace.decodeFormula(x);
        this.y = namespace.decodeFormula(y);
        this.z = namespace.decodeFormula(z);
        updateMovement();
        cache = Pair.of(startTime, Vec3.ZERO);
        namespace.assign("start_time", startTime);
        namespace.assign("end_time", endTime);
    }

    public CustomTranslation(String x, String y, String z, float startTime, float endTime) {
        this(new Namespace(ROOT_NAMESPACE), x, y, z, startTime, endTime);
    }

    public CustomTranslation(Namespace namespace, float startTime, float endTime) {
        this(namespace, "0", "0", "0", startTime, endTime);
    }

    public CustomTranslation(float startTime, float endTime) {
        this("0", "0", "0", startTime, endTime);
    }

    public void updateMovement() {
        float t = namespace.getInstance("t").getValue("t");
        namespace.assign("t", 1f);
        setData(new Vec3(x.getResult(), y.getResult(), z.getResult()));
        namespace.assign("t", t);
    }

    public Pair<Float, Vec3> getCache() {
        return cache;
    }

    public void setX(String x) {
        this.x = namespace.decodeFormula(x);
        updateMovement();
    }

    public void setY(String y) {
        this.y = namespace.decodeFormula(y);
        updateMovement();
    }

    public void setZ(String z) {
        this.z = namespace.decodeFormula(z);
        updateMovement();
    }

    public Formula getX() {
        return x;
    }

    public Formula getY() {
        return y;
    }

    public Formula getZ() {
        return z;
    }

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
    public void reset() {
        super.reset();
        this.x = namespace.decodeFormula("0");
        this.y = namespace.decodeFormula("0");
        this.z = namespace.decodeFormula("0");
        cache = Pair.of(0f, Vec3.ZERO);
    }

    @Override
    public Vec3 getPercentage(float percentage) {
        if (cache.getFirst().equals(percentage))
            return cache.getSecond();
        Vec3 result = InterpolationUtil.custom(namespace, x, y, z, percentage);
        cache = Pair.of(percentage, result);
        return result;
    }

    @Override
    public void move(float time, PoseStack pose) {
        VectorUtil.translate(pose, getPercentage(time));
    }

    @Override
    public void apply(PivotPoint point, float time) {
        point.translate(getPercentage(time));
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
