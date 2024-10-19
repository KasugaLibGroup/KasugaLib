package kasuga.lib.example_env.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.client.animation.data.Animation;
import kasuga.lib.core.client.render.model.MultiPartModel;
import kasuga.lib.core.client.render.texture.Matrix;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.example_env.entity.WuLingEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class WuLingRenderer extends EntityRenderer<WuLingEntity> {
    public WuLingRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(WuLingEntity pEntity) {
        return new ResourceLocation(KasugaLib.MOD_ID, "");
    }

    @Override
    public void render(WuLingEntity entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffer, int light) {
        /*
        Animation test_anim = entity.getAnimation();
        pose.pushPose();
        if(test_anim != null) {
            test_anim.getModel().translate(0, 0, -.5);
            test_anim.assign(entity, partial);
            entity.actAnimation();
            test_anim.action();
            test_anim.getModel().render(pose, buffer, 0, 0, 0, (int)(light * .9), 0);
        }
        pose.popPose();

         */
    }
}
