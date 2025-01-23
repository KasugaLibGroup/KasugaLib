package kasuga.lib.core.events.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderFluidMaskEvent {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void renderFluidOverlay(RenderBlockOverlayEvent event) {
        if (event.getOverlayType() != RenderBlockOverlayEvent.OverlayType.WATER)
            return;
        PoseStack pose = event.getPoseStack();
        BlockState state = event.getPlayer().getLevel().getBlockState(event.getBlockPos());
        if (state.is(Blocks.WATER) || state.is(Blocks.LAVA)) return;
        Block rawBlock = state.getBlock();
        if (!(rawBlock instanceof LiquidBlock block)) return;
        Fluid fluid  = block.getFluid();
        FluidAttributes attributes = fluid.getAttributes();
        ResourceLocation overlay = attributes.getOverlayTexture();
        if (overlay == null) return;
        renderOverlay(pose, attributes.getOverlayTexture());
        event.setCanceled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderOverlay(PoseStack pose, ResourceLocation overlay) {
        ResourceLocation rl = new ResourceLocation(overlay.getNamespace(), "textures/" + overlay.getPath() + ".png");
        Minecraft pMinecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.enableTexture();
        RenderSystem.setShaderTexture(0, rl);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        float f = pMinecraft.player.getBrightness();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(f, f, f, 0.1F);
        float f7 = -pMinecraft.player.getYRot() / 64.0F;
        float f8 = pMinecraft.player.getXRot() / 64.0F;
        pose.pushPose();
        Matrix4f matrix4f = pose.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).uv(4.0F + f7, 4.0F + f8).endVertex();
        bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).uv(0.0F + f7, 4.0F + f8).endVertex();
        bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).uv(0.0F + f7, 0.0F + f8).endVertex();
        bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).uv(4.0F + f7, 0.0F + f8).endVertex();
        bufferbuilder.end();
        BufferUploader.end(bufferbuilder);
        RenderSystem.disableBlend();
        pose.popPose();
    }
}
