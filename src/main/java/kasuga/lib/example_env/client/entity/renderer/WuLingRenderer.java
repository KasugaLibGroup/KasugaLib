package kasuga.lib.example_env.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.animation.Constants;
import kasuga.lib.core.client.animation.data.Animation;
import kasuga.lib.core.client.render.model.MultiPartModel;
import kasuga.lib.example_env.AllExampleElements;
import kasuga.lib.example_env.entity.WuLingEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class WuLingRenderer extends EntityRenderer<WuLingEntity> {
    public final MultiPartModel wuling_model;
    public final Animation test_anim;
    public WuLingRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);

        wuling_model = ((MultiPartModel) AllExampleElements.wuLingVans.getModel()).clone();
        wuling_model.renderType(RenderType::solid);
        wuling_model.applyParentRenderTypeForAllBones();
        Optional<Animation> optional = Animation.decode(Constants.root(),
                new ResourceLocation(KasugaLib.MOD_ID, "models/entity/test/wuling/wuling_anim.json"),
                "test_anim");
        test_anim = optional.orElse(null);
        if(test_anim != null) {
            test_anim.loadModel(wuling_model);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(WuLingEntity pEntity) {
        return new ResourceLocation(KasugaLib.MOD_ID, "");
    }

    @Override
    public void render(WuLingEntity entity, float yaw, float partial, PoseStack pose, MultiBufferSource buffer, int light) {
        wuling_model.translate(0, 0, -.5);
        if(test_anim != null) {
            test_anim.assign(entity, partial);
            test_anim.assign("left_front", entity.doorControl.isLeftFront() ? 1 : 0);
            test_anim.assign("left_back", entity.doorControl.isLeftBack() ? 1 : 0);
            test_anim.assign("right_front", entity.doorControl.isRightFront() ? 1 : 0);
            test_anim.assign("right_back", entity.doorControl.isRightBack() ? 1 : 0);
            test_anim.assign("mid_back", entity.doorControl.isMidBack() ? 1 : 0);
            test_anim.action();
        }
        wuling_model.render(pose, buffer, 0, 0, 0, (int)(light * .9), 0);
    }
}
