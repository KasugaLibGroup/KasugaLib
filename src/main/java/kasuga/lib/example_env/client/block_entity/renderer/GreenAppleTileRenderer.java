package kasuga.lib.example_env.client.block_entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.render.font.TextContext;
import kasuga.lib.core.client.render.model.SimpleModel;
import kasuga.lib.core.client.render.texture.Matrix;
import kasuga.lib.core.client.render.texture.old.WorldTexture;
import kasuga.lib.core.model.BedrockModelLoader;
import kasuga.lib.core.model.anim_instance.AnimationInstance;
import kasuga.lib.core.model.anim_json.Animation;
import kasuga.lib.core.model.anim_model.AnimModel;
import kasuga.lib.core.util.LazyRecomputable;
import kasuga.lib.example_env.AllClient;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.example_env.block_entity.GreenAppleTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.util.Map;

public class GreenAppleTileRenderer implements BlockEntityRenderer<GreenAppleTile> {
    // SimpleModel model = AllExampleElements.greenAppleModel.getModel();
    // MultiPartModel wuling = (MultiPartModel) AllExampleElements.wuLingVans.getModel();
    // private static final WorldTexture texture = new WorldTexture(new ResourceLocation(KasugaLib.MOD_ID, "textures/common/test/green_apple_bubble.png"));


    LazyRecomputable<AnimModel> testModel = LazyRecomputable.of(() -> {
            AnimModel model = BedrockModelLoader.getModel(AllExampleElements.REGISTRY.asResource("block/test/test_model_complicate"), RenderType.solid());
            if (model == null) return null;
            return model.copy();
        }
    );

    LazyRecomputable<AnimationInstance> transform = LazyRecomputable.of(() -> {
        AnimationInstance ai = AllClient.anim.get().getAnimation("transform").getInstance(testModel.get(), 60);
        return ai;
    });

    public GreenAppleTileRenderer(BlockEntityRendererProvider.Context context) {}
    // private WorldTexture TEXTURE = new WorldTexture(new ResourceLocation("kasuga_lib","textures/gui/pixel.png"));
    @Override
    public void render(GreenAppleTile tile, float partial, PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();

        // textContext.rotateDeg(1f, 1f, 1f);
            transform.get().applyAndRender(pose, buffer, light, overlay, Math.max(0, Math.min(tile.sec, 0f)));
        if (tile.sec < 5f && !tile.direction) tile.sec += 0.01f;
        else if (tile.sec >= 5f && !tile.direction) tile.direction = true;
        if (tile.sec > -2.5f && tile.direction) tile.sec -= 0.01f;
        else if (tile.sec <= -2.5f && tile.direction) tile.direction = false;
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
