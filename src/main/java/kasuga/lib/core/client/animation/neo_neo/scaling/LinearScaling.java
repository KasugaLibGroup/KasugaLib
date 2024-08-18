package kasuga.lib.core.client.animation.neo_neo.scaling;

import net.minecraft.world.phys.Vec3;

public class LinearScaling extends Scaling {

    public LinearScaling(Vec3 data, float startTime, float endTime) {
        super(data, startTime, endTime);
    }

    public void setScale(Vec3 scale) {
        super.setData(scale);
    }

    public Vec3 getScale() {
        return getData();
    }

    public void scale(Vec3 scale) {
        this.setScale(getScale().multiply(scale));
    }

    @Override
    public Vec3 getPercentage(float percentage) {
        return STARTER.add(STARTER.subtract(getData()).scale(percentage));
    }
}
