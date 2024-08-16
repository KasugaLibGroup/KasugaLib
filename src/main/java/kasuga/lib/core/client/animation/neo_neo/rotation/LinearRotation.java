package kasuga.lib.core.client.animation.neo_neo.rotation;

import kasuga.lib.core.client.animation.neo_neo.InterpolationUtil;
import net.minecraft.world.phys.Vec3;

public class LinearRotation extends Rotation {
    public LinearRotation(Vec3 data, float startTime, float endTime, boolean degree) {
        super(data, startTime, endTime, degree);
    }

    @Override
    public Vec3 getPercentage(float percentage) {
        return InterpolationUtil.linear(this.data, percentage);
    }
}
