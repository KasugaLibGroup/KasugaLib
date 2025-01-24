package kasuga.lib.core.util.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.client.model.AnimModelLoader;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import kasuga.lib.core.util.LazyRecomputable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static kasuga.lib.core.util.projectile.PanelRenderer.BASE_OFFSET;

public class RayRenderer {

    private static final LazyRecomputable<AnimModel> arrowModel = LazyRecomputable.of(
            () -> {
                AnimModel model = AnimModelLoader.INSTANCE.getModel(new ResourceLocation(KasugaLib.MOD_ID, "arrow"));
                model.init();
                return model;
            }
    );

    private final Ray ray;
    public RayRenderer(Ray ray) {
        this.ray = ray;
    }

    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();
        Vector3f source = ray.getSource();
        pose.translate(source.x(), source.y(), source.z());
        Quaternionf quaternion = Panel.getQuaternion(ray.getForward());
        pose.mulPose(quaternion);
        pose.translate(-BASE_OFFSET.x(), BASE_OFFSET.y(), -BASE_OFFSET.z());
        arrowModel.get().render(pose, buffer, light, overlay);
        pose.popPose();
    }
}
