package kasuga.lib.core.client.animation.neo_neo.scaling;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.client.animation.neo_neo.point.PivotPoint;
import kasuga.lib.core.client.animation.neo_neo.point.Point;
import net.minecraft.world.phys.Vec3;

public abstract class Scaling extends Movement {
    public static final Vec3 STARTER = new Vec3(1, 1, 1);

    public Scaling(Vec3 data, float startTime, float endTime) {
        super(data, startTime, endTime);
    }

    @Override
    public void move(float time, PoseStack pose) {
        VectorUtil.scale(pose, getPercentage(calculateTime(time)));
    }

    @Override
    public void apply(PivotPoint point, float time) {
        if (point instanceof Point p)
            p.scale(getPercentage(calculateTime(time)));
    }
}
