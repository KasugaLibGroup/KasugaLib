package kasuga.lib.example_env.client.block_entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.core.client.model.BedrockModelLoader;
import kasuga.lib.core.client.model.anim_instance.AnimateTicker;
import kasuga.lib.core.client.model.anim_instance.AnimationInstance;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.example_env.block.green_apple.GreenAppleTile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class GreenAppleTileRenderer implements BlockEntityRenderer<GreenAppleTile> {
    // SimpleModel model = AllExampleElements.greenAppleModel.getModel();
    // MultiPartModel wuling = (MultiPartModel) AllExampleElements.wuLingVans.getModel();
    // private static final WorldTexture texture = new WorldTexture(new ResourceLocation(KasugaLib.MOD_ID, "textures/common/test/green_apple_bubble.png"));

    LazyRecomputable<AnimateTicker> ticker = AnimateTicker.getTickerInstance(
            AllExampleElements.testRegistry.asResource("block/test/test_model_complicate"),
            AllExampleElements.testRegistry.asResource("model"),
            RenderType.solid(), "transform", AnimateTicker.TickerType.RENDER, 60, 100);

    public GreenAppleTileRenderer(BlockEntityRendererProvider.Context context) {}
    // private WorldTexture TEXTURE = new WorldTexture(new ResourceLocation("kasuga_lib","textures/gui/pixel.png"));
    @Override
    public void render(GreenAppleTile tile, float partial, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();

        // textContext.rotateDeg(1f, 1f, 1f);
        ticker.get().tickAndRender(pose, buffer, light, overlay, partial);
        if (tile.sec < 10) tile.sec+=0.01;
        if (tile.sec >= 10 && !tile.saved) {
            tile.saved = true;
            ticker.get().start();tile.sec++;
        }
        // if (tile.sec < 5f && !tile.direction) tile.sec += 0.01f;
        // else if (tile.sec >= 5f && !tile.direction) tile.direction = true;
        // if (tile.sec > -2.5f && tile.direction) tile.sec -= 0.01f;
        // else if (tile.sec <= -2.5f && tile.direction) tile.direction = false;
        pose.popPose();
        // System.out.println(mtx.equals(mtx2));
        // pose.translate(0, -1, 0);
        // textContext2.renderToWorld(pose, buffer, light);
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
