package kasuga.lib.example_env.client.block_entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.frontend.rendering.RenderContext;
import kasuga.lib.core.client.render.component.SimpleComponent;
import kasuga.lib.core.client.render.model.SimpleModel;
import kasuga.lib.core.client.render.texture.WorldTexture;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.example_env.block_entity.GreenAppleTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GreenAppleTileRenderer implements BlockEntityRenderer<GreenAppleTile> {
    SimpleModel model = AllExampleElements.greenAppleModel.getModel();
    // MultiPartModel wuling = (MultiPartModel) AllExampleElements.wuLingVans.getModel();
    SimpleComponent component = new SimpleComponent(Component.literal("QwQ"));
    private static final WorldTexture texture = new WorldTexture(new ResourceLocation(KasugaLib.MOD_ID, "textures/common/test/green_apple_bubble.png"));
    public GreenAppleTileRenderer(BlockEntityRendererProvider.Context context) {
        texture.renderType(RenderType::text);
        TEXTURE.renderType(RenderType::text);
        // model.renderType(RenderType::translucent);
        // wuling.renderType(RenderType::solid);
        // wuling.applyParentRenderTypeForAllBones();
        /*
        wuling.setStaticMovements(
                m -> {
                    m.translateBone("front_door_left", -1.18, 0, -1.55);
                    m.translateBone("front_door_right", 1.18, 0, -1.55);
                    m.translateBone("back_door_left", -1.23, 0, 0);
                    m.translateBone("back_door_right", 1.23, 0, 0);
                    m.translateBone("large_back_door", 0, 2.5, 3.2);
                    m.translateBone("left_wheel", -1, 0.5, -1.975);
                    m.translateBone("right_wheel", 1, 0.5, -1.975);
                    m.translateBone("left_wheel_2", -1, 0.5, 2.05);
                    m.translateBone("right_wheel_2", 1, 0.5, 2.05);
                    m.translateBone("tie", -0.65, 1.6, -1.45);
                    m.rotateXForBone("tie", -30f);
                }
        );

         */
        // component.setFont(Minecraft.getInstance().font);
        // component.zoom(component.getZoom() * .2f);
    }
    private WorldTexture TEXTURE = new WorldTexture(new ResourceLocation("kasuga_lib","textures/gui/pixel.png"));
    @Override
    public void render(GreenAppleTile tile, float partial, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
/*
        BlockPos pos = tile.getBlockPos();
        pose.pushPose();
        TEXTURE.render(pose,buffer,0.25f,0.25f, light);
        pose.popPose();

        pose.pushPose();
        pose.scale(0.01F, 0.01F, 0.001F);
        RenderContext renderContext = new RenderContext(RenderContext.RenderContextType.WORLD);
        renderContext.setSource(tile);
        renderContext.setPoseStack(pose);
        renderContext.setBufferSource(buffer);
        tile.getGui().getContext().ifPresent((context)->{
            context(tile,400,200);
            context.render(renderContext);
        });
        pose.popPose();
*/
        // model.translate(0, -.5, 0);
        // model.render(pose, buffer, 0, -.5f, 0, light, overlay);
        // component.turnToPlayer(Minecraft.getInstance().player, RendererUtil.blockPos2Vec3(tile.getBlockPos()));
        // component.translate(0, 1.55f, .01f);
        // component.renderCenteredInWorld(pose, buffer, light);
        // wuling.translate(.5, 0, .5);
        // wuling.render(pose, buffer, 0, 0, 0, (int)(light * .9), overlay);
    }

    @Override
    public boolean shouldRenderOffScreen(GreenAppleTile pBlockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
