package kasuga.lib.mixins.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface MixinGameRenderer {
    @Invoker("getFov")
    public double kasugalib$invokeGetFov(Camera pActiveRenderInfo, float pPartialTicks, boolean pUseFOVSetting);
}
