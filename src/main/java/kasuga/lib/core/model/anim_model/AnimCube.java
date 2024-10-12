package kasuga.lib.core.model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.model.BedrockRenderable;
import kasuga.lib.core.model.base.Bone;
import kasuga.lib.core.model.base.Cube;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.List;
import java.util.Map;

public class AnimCube implements BedrockRenderable {

    public final Cube cube;
    private final List<BakedQuad> quads;

    public final AnimModel model;

    // TODO: 要把绝对位移转化为相对位移
    private final AnimBone bone;
    public AnimCube(Cube cube, AnimModel model, AnimBone bone) {
        this.cube = cube;
        quads = cube.getBaked(model.material.sprite());
        this.model = model;
        this.bone = bone;
    }
    @Override
    @Deprecated
    public Map<String, BedrockRenderable> getChildrens() {
        return Map.of();
    }

    @Override
    public void applyTranslationAndRotation(PoseStack pose) {
        Vector3f translation = cube.getPivot().copy();
        translation.mul(1 / 16f);
        Vector3f parentTrans = bone.getPivot().copy();
        parentTrans.mul(1 / 16f);
        Vector3f t = vonvertPivot(translation, parentTrans);
        pose.translate(t.x(), t.y(), t.z());

        Vector3f rotation = cube.getRotation();
        if (rotation.equals(Vector3f.ZERO)) return;
        pose.mulPose(Vector3f.ZP.rotationDegrees(rotation.z()));
        pose.mulPose(Vector3f.YP.rotationDegrees(rotation.y()));
        pose.mulPose(Vector3f.XP.rotationDegrees(rotation.x()));
    }

    @Override
    @Deprecated
    public BedrockRenderable getChild(String name) {
        return null;
    }

    public void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay) {
        pose.pushPose();
        applyTranslationAndRotation(pose);
        quads.forEach(baked -> consumer.putBulkData(pose.last(), baked,
                color.getfR(), color.getfG(), color.getfB(), light, overlay));
        pose.popPose();
    }
}
