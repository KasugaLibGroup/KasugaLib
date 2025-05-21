package kasuga.lib.core.client.model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.model.BedrockRenderable;
import kasuga.lib.core.client.model.model_json.Cube;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AnimCube implements BedrockRenderable {

    public final Cube cube;
    private final List<BakedQuad> quads;

    public final AnimModel model;
    private final AnimBone bone;
    private final Vector3f pivot, rotation;
    public AnimCube(Cube cube, AnimModel model, AnimBone bone) {
        this.cube = cube;

        Vector3f pos = cube.getPivot().copy();
        pos.mul(-1 / 16f);
        quads = cube.getBaked(model.geometry.getModel().getTextureMaterial().sprite(), pos);

        this.pivot = cube.getPivot().copy();
        pivot.mul(1 / 16f);

        this.rotation = cube.getRotation().copy();

        this.model = model;
        this.bone = bone;
    }

    protected AnimCube(Cube cube, List<BakedQuad> quads, AnimModel model,
                       AnimBone bone, Vector3f pivot, Vector3f rotation) {
        this.cube = cube;
        this.quads = new ArrayList<>(quads.size());
        this.quads.addAll(quads);
        this.model = model;
        this.bone = bone;
        this.pivot = pivot.copy();
        this.rotation = rotation.copy();
    }

    @Override
    @Deprecated
    public Map<String, BedrockRenderable> getChildrens() {
        return Map.of();
    }

    @Override
    public void applyTranslationAndRotation(PoseStack pose) {
        Vector3f translation = pivot.copy();
        Vector3f parentTrans = bone.getPivot().copy();
        Vector3f t = convertPivot(translation, parentTrans);
        pose.translate(t.x(), t.y(), t.z());

        Vector3f rotation = this.rotation.copy();
        if (rotation.equals(Vector3f.ZERO)) return;
        pose.mulPose(Vector3f.ZP.rotationDegrees(rotation.z()));
        pose.mulPose(Vector3f.YN.rotationDegrees(rotation.y()));
        pose.mulPose(Vector3f.XN.rotationDegrees(rotation.x()));
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

    public AnimCube copy(AnimModel model, AnimBone bone) {
        return new AnimCube(cube, this.quads, model, bone, this.pivot, this.rotation);
    }
}
