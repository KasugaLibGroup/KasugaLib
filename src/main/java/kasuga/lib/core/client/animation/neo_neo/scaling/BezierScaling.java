package kasuga.lib.core.client.animation.neo_neo.scaling;

import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.IBezier;
import kasuga.lib.core.client.animation.neo_neo.InterpolationUtil;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;

public class BezierScaling extends Scaling implements IBezier {
    private final LinkedList<Vec3> bezierPoints;
    private Pair<Float, Vec3> cache;
    public BezierScaling(Vec3 data, float startTime, float endTime, List<Vec3> bezierPoints) {
        super(data, startTime, endTime);
        this.bezierPoints = new LinkedList<>(bezierPoints);
        cache = Pair.of(0f, STARTER);
    }

    public BezierScaling(Vec3 data, float startTime, float endTime) {
        this(data, startTime, endTime, List.of());
    }

    public Pair<Float, Vec3> getCache() {
        return cache;
    }

    @Override
    public Vec3 getPercentage(float percentage) {
        if (cache.getFirst().equals(percentage))
            return cache.getSecond();
        Vec3 result = InterpolationUtil.bezier(STARTER, getData(), bezierPoints, percentage);
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
