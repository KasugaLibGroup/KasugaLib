package kasuga.lib.mixins.mixin.create;

import com.simibubi.create.content.trains.entity.TravellingPoint;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = TravellingPoint.class,remap = false)
public class MixinTravelingPoint {

}
