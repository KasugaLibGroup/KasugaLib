package kasuga.lib.mixins.mixin.client;

import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntityRenderer;
import com.simibubi.create.foundation.utility.NBTHelper;
import kasuga.lib.core.create.BogeyDataConstants;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

                        // 这里改成 true 就可以返回正确的朝向数据，如果为 false 则不改变从 Carriage 返回的值
                        // 以下为对应方向的转置顺序, 前者为从Carriage中拿到的方块，后者为正确朝向
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
