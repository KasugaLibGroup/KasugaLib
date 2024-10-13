package kasuga.lib.core.model.anim_model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.model.BedrockRenderable;
import kasuga.lib.core.model.base.Bone;
import kasuga.lib.core.model.base.Cube;
import kasuga.lib.core.model.base.Quad;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnimBone implements BedrockRenderable, Animable {

    public final Bone bone;
    private final Vector3f pivot, rotation;
    private final Map<String, BedrockRenderable> children;
    private final List<BedrockRenderable> cubes;

    public final AnimModel model;
    private AnimBone parent;
    private Vector3f offset, animRot, scale;

    public AnimBone(Bone bone, AnimModel model) {
        this.bone = bone;
        this.pivot = bone.pivot.copy();
        this.rotation = bone.rotation.copy();
        pivot.mul(1 / 16f);
        children = Maps.newHashMap();
        this.model = model;
        this.cubes = Lists.newArrayList();
        collectCubes();
    }

    public AnimBone(Bone bone, AnimModel model, Map<String, BedrockRenderable> children, List<BedrockRenderable> cubes) {
        this.bone = bone;
        this.children = children;
        this.model = model;
        this.cubes = cubes;

        this.pivot = bone.pivot.copy();
        this.rotation = bone.rotation.copy();
        pivot.mul(1 / 16f);

    }

    public void collectCubes() {
        List<Cube> cubes = bone.getCubes();
        for (Cube cube : cubes) {
            AnimCube ac = new AnimCube(cube, this.model, this);
            this.cubes.add(ac);
        }
    }

    public void addThisToParent() {
        BedrockRenderable parent = model.getChild(this.bone.parent);
        if (!(parent instanceof AnimBone parentBone)) return;
        Map<String, BedrockRenderable> map = parentBone.getChildrens();
        map.put(this.bone.name, this);
        this.parent = (AnimBone) model.getChild(this.bone.parent);
    }


    @Override
    public Map<String, BedrockRenderable> getChildrens() {
        return children;
    }

    @Override
    public BedrockRenderable getChild(String name) {
        return children.getOrDefault(name, null);
    }

    public BedrockRenderable getChild(int index) {
        return children.getOrDefault(String.valueOf(index), null);
    }

    public Vector3f getPivot() {
        return this.pivot.copy();
    }

    public void resetAnimation() {
        this.initAnim();
    }

    private void initAnim() {
        this.offset = Vector3f.ZERO.copy();
        this.animRot = Vector3f.ZERO.copy();
        this.scale = new Vector3f(1, 1, 1);
    }

    private Vector3f getRealPosition() {
        Vector3f result = this.pivot.copy();
        result.add(this.offset);
        return result;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    private Vector3f getRealRotation() {
        Vector3f result = this.rotation.copy();
        result.add(this.animRot);
        return result;
    }

    public void setAnimRot(Vector3f animRot) {
        this.animRot = animRot;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    @Override
    public void applyTranslationAndRotation(PoseStack pose) {
        Vector3f translation = getRealPosition();
        Vector3f parentTrans = parent != null ? parent.getPivot().copy() : Vector3f.ZERO.copy();
        Vector3f t = vonvertPivot(translation, parentTrans);
        pose.translate(t.x(), t.y(), t.z());

        Vector3f rotation = getRealRotation();
        if (rotation.equals(Vector3f.ZERO)) return;
        pose.mulPose(Vector3f.ZP.rotationDegrees(rotation.z()));
        pose.mulPose(Vector3f.YN.rotationDegrees(rotation.y()));
        pose.mulPose(Vector3f.XN.rotationDegrees(rotation.x()));

        if (scale.equals(Cube.BASE_SCALE)) return;
        pose.scale(scale.x(), scale.y(), scale.z());
    }

    @Override
    public void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay) {
        pose.pushPose();
        applyTranslationAndRotation(pose);
        cubes.forEach(c -> c.render(pose, consumer, color, light, overlay));
        children.forEach((c, d) -> d.render(pose, consumer, color, light, overlay));
        pose.popPose();
    }
}
