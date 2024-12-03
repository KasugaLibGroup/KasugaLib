package kasuga.lib.mixins.mixin.client;

import com.mojang.blaze3d.vertex.VertexFormat;
import kasuga.lib.core.client.model.NamedRenderTypeManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderType.class)
public class MixinRenderType {

    @Inject(method = "create(Ljava/lang/String;Lcom/mojang/blaze3d/vertex/VertexFormat;Lcom/mojang/blaze3d/vertex/VertexFormat$Mode;ILnet/minecraft/client/renderer/RenderType$CompositeState;)Lnet/minecraft/client/renderer/RenderType$CompositeRenderType;",
            at = @At("RETURN"))
    private static void doCreate(String pName, VertexFormat pFormat, VertexFormat.Mode pMode,
                                 int pBufferSize, RenderType.CompositeState pState,
                                 CallbackInfoReturnable<RenderType> cir) {
        RenderType type = cir.getReturnValue();
        NamedRenderTypeManager.INSTANCE.put(new ResourceLocation(pName), type);
    }
}
