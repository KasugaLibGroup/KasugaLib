package kasuga.lib.example_env.block.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.menu.targets.Target;
import kasuga.lib.core.menu.targets.WorldRendererTarget;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class GuiExampleBlockRenderer implements BlockEntityRenderer<GuiExampleBlockEntity> {
    public GuiExampleBlockRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(GuiExampleBlockEntity entity, float v, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
        RenderContext worldContext = new RenderContext(RenderContext.RenderContextType.WORLD);
        worldContext.setBufferSource(multiBufferSource);
        worldContext.setPoseStack(poseStack);
        worldContext.pushLight(light);
        worldContext.pushLight(LightTexture.FULL_BRIGHT);
        worldContext.setSource(WorldRendererTarget.class);
        poseStack.pushPose();
        poseStack.scale(0.0025f,0.0025f,0.0025f);
        entity.menuEntry.getBinding().apply(Target.WORLD_RENDERER).render(worldContext);
        poseStack.popPose();
    }
}
