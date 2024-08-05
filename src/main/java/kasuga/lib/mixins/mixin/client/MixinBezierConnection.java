package kasuga.lib.mixins.mixin.client;

import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import kasuga.lib.KasugaLib;
import kasuga.lib.registrations.create.TrackMaterialReg;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BezierConnection.class)
public class MixinBezierConnection {

    @Shadow(remap = false)
    protected
    TrackMaterial trackMaterial;
    @Redirect(method = "getBakedGirders", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 doScale(Vec3 instance, double factor) {
        return kasugaLib$innerScale(instance, factor);
    }

    @Redirect(method = "getBakedSegments", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"))
    public Vec3 doScale2(Vec3 instance, double factor) {
        return kasugaLib$innerScale(instance, factor);
    }

    @Unique
    protected final Vec3 kasugaLib$innerScale(Vec3 instance, double factor) {
        if (factor != .965f) return instance.scale(factor);
        KasugaLib.MAIN_LOGGER.error("innerScale");
        TrackMaterialReg reg = KasugaLib.STACKS.getCachedTrackMaterial(trackMaterial);
        if (reg == null) return instance.scale(factor);
        return reg.trackOffsets().apply(instance);
    }
}
