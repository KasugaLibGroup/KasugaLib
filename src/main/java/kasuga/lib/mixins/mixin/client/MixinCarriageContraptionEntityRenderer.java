package kasuga.lib.mixins.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntityRenderer;
import com.simibubi.create.foundation.utility.NBTHelper;
import kasuga.lib.core.create.BogeyDataConstants;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CarriageContraptionEntityRenderer.class, remap = false)
public class MixinCarriageContraptionEntityRenderer {

    @Redirect(method = "render(Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/trains/entity/CarriageContraptionEntity;getPosition(F)Lnet/minecraft/world/phys/Vec3;"),
            remap = false)
    private Vec3 doGetPosition(CarriageContraptionEntity instance, float partial_ticks){
        Carriage carriage = instance.getCarriage();

        if(carriage != null) {
            carriage.bogeys.forEach(
                    bogey -> {
                        Direction d1 = instance.getInitialOrientation();
                        Direction d2 = d1;

                        // North -> West
                        // South -> East
                        // West -> South
                        // East -> North

                        d2 = Direction.fromYRot(d1.toYRot() + 90);
                        if(bogey != null) {
                            NBTHelper.writeEnum(bogey.bogeyData, BogeyDataConstants.BOGEY_ASSEMBLY_DIRECTION_KEY, d2);
                            bogey.bogeyData.putFloat("PartialTicks", partial_ticks);
                        }
                    }
            );
        }
        return instance.getPosition(partial_ticks);
    }
}
