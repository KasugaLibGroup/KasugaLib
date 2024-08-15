package kasuga.lib.core.client.animation.neo_neo.translation;

import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.IBezier;
import kasuga.lib.core.client.animation.neo_neo.InterpolationUtil;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BezierTranslation extends Translation implements IBezier {
    private final List<Vec3> bezierPoints;
    Pair<Float, Vec3> cache = Pair.of(0f, Vec3.ZERO);
    public BezierTranslation(Vec3 data, float startTime, float endTime) {
        super(data, startTime, endTime);
        this.bezierPoints = new LinkedList<>();
    }

    public BezierTranslation(Vec3 data, float startTime, float endTime, List<Vec3> bezierPoints) {
        this(data, startTime, endTime);
        this.bezierPoints.addAll(bezierPoints);
    }

    @Override
    public void reset() {
        bezierPoints.clear();
        cache = Pair.of(0f, Vec3.ZERO);
        super.reset();
    }

    @Override
    public Vec3 getPercentage(float percentage) {
        if (cache.getFirst().equals(percentage))
            return cache.getSecond();
        Vec3 result = InterpolationUtil.bezier(Vec3.ZERO, data, bezierPoints, percentage);
        cache = Pair.of(percentage, result);
        return result;
    }

    @Override
    public List<Vec3> getBezierPoints() {
        return bezierPoints;
    }

    @Override
    public Vec3 getMovementVector() {
        return getData();
    }

    @Override
    public void createDefaultBezier(IBezier before) {
        InterpolationUtil.createDefaultBezier(before, this, 1f/3f);
    }

    @Override
    public void write(CompoundTag nbt) {
        super.write(nbt);
        VectorIOUtil.writeVec3ListToNbt(nbt, "bezier", bezierPoints);
    }

    @Override
    public void read(CompoundTag nbt) {
        super.read(nbt);
        bezierPoints.clear();
        bezierPoints.addAll(VectorIOUtil.getVec3ListFromNbt(nbt, "bezier"));
    }
}
