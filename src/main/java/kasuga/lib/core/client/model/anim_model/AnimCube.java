package kasuga.lib.core.client.model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kasuga.lib.core.client.animation.neo_neo.rotation.Rotation;
import kasuga.lib.core.client.model.Rotationable;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.model.BedrockRenderable;
import kasuga.lib.core.client.model.model_json.Cube;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

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

        Vector3f pos = new Vector3f(cube.getPivot());
        pos.mul(-1 / 16f);
        quads = cube.getBaked(model.geometry.getModel().getTextureMaterial().sprite(), pos);

        this.pivot = new Vector3f(cube.getPivot());
        pivot.mul(1 / 16f);

        this.rotation = new Vector3f(cube.getRotation());

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
        this.pivot = new Vector3f(pivot);
        this.rotation = new Vector3f(rotation);
    }

    @Override
    @Deprecated
    public Map<String, BedrockRenderable> getChildrens() {
        return Map.of();
    }

    @Override
    public void applyTranslationAndRotation(PoseStack pose) {
        Vector3f translation = new Vector3f(pivot);
        Vector3f parentTrans = new Vector3f(bone.getPivot());
        Vector3f t = convertPivot(translation, parentTrans);
        pose.translate(t.x(), t.y(), t.z());

        Vector3f rotation = new Vector3f(this.rotation);
        if (rotation.equals(Rotationable.ZERO)) return;
        pose.mulPose(Axis.ZP.rotationDegrees(rotation.z()));
        pose.mulPose(Axis.YN.rotationDegrees(rotation.y()));
        pose.mulPose(Axis.XN.rotationDegrees(rotation.x()));
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
