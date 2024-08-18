package kasuga.lib.core.client.animation.neo_neo.translation;

import net.minecraft.world.phys.Vec3;

public class LinearTranslation extends Translation {
    public LinearTranslation(Vec3 data, float startTime, float endTime) {
        super(data, startTime, endTime);
    }

    @Override
    public Vec3 getPercentage(float percentage) {
        return data.scale(percentage);
    }
}
