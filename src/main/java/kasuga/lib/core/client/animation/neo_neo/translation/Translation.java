package kasuga.lib.core.client.animation.neo_neo.translation;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.animation.neo_neo.base.Movement;
import kasuga.lib.core.client.animation.neo_neo.point.PivotPoint;
import net.minecraft.world.phys.Vec3;

public abstract class Translation extends Movement {
    public Translation(Vec3 data, float startTime, float endTime) {
        super(data, startTime, endTime);
    }

    @Override
    public void move(float time, PoseStack pose) {
        VectorUtil.translate(pose, getPercentage(calculateTime(time)));
    }

    public void translate(Vec3 vec3) {
        this.data = this.data.add(vec3);
    }

    public void translate(double x, double y, double z) {
        this.data = this.data.add(x, y, z);
    }

    @Override
    public void apply(PivotPoint point, float time) {
        point.translate(getPercentage(calculateTime(time)));
    }
}
