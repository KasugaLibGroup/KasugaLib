package kasuga.lib.core.client.model.anim_model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import kasuga.lib.core.client.model.Rotationable;
import kasuga.lib.core.client.model.model_json.Geometry;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.model.BedrockRenderable;
import kasuga.lib.core.client.model.model_json.Cube;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;
import org.joml.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AnimModel implements Animable {

    public final Map<String, BedrockRenderable> children;
    public final List<BedrockRenderable> roots;
    public final Geometry geometry;
    public final Material material;
    public final RenderType renderType;
    private SimpleColor color;

    public Vector3f position, rotation,
                    offset, animRot, scale;

    public AnimModel(Geometry geometry, Material material, RenderType renderType) {
        this.geometry = geometry;
        this.children = Maps.newHashMap();
        this.material = material;
        this.renderType = renderType;
        position = new Vector3f(Cube.BASE_OFFSET);
        rotation = new Vector3f();
        color = SimpleColor.fromRGBInt(0xffffff);
        roots = Lists.newArrayList();
        collectChildren();
        initAnim();
    }

    protected AnimModel(Geometry geometry, Material material, RenderType renderType,
                      Vector3f position, Vector3f rotation, SimpleColor color) {
        this.geometry = geometry;
        this.material = material;
        this.renderType = renderType;
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.color = color.copy();
        this.children = Maps.newHashMap();
        this.roots = Lists.newArrayList();
        initAnim();
    }

    public AnimModel(Geometry geometry, Material material, RenderType renderType,
                     Map<String, BedrockRenderable> children, List<BedrockRenderable> roots) {
        this.geometry = geometry;
        this.children = children;
        this.material = material;
        this.renderType = renderType;
        position = new Vector3f();
        rotation = new Vector3f();
        this.roots = roots;
        color = SimpleColor.fromRGBInt(0xffffff);
        initAnim();
    }

    public void resetAnimation() {
        this.initAnim();
    }

    private void initAnim() {
        this.offset = new Vector3f();
        this.animRot = new Vector3f();
        this.scale = new Vector3f(1, 1, 1);
    }

    public void collectChildren() {
        geometry.getBones().forEach((name, bone) -> {
            AnimBone ab = new AnimBone(bone, this);
            children.put(name, ab);
        });
        dealWithDependency();
    }

    public void dealWithDependency() {
        children.forEach((name, ren) -> {
            if (!(ren instanceof AnimBone ab)) return;
            ab.addThisToParent();
        });
        children.forEach((name, ren) -> {
            if (!(ren instanceof AnimBone b)) return;
            if (b.bone.hasParent()) return;
            roots.add(b);
        });
    }


    public Map<String, BedrockRenderable> getChildrens() {
        return children;
    }


    public BedrockRenderable getChild(String name) {
        return children.getOrDefault(name, null);
    }


    public void applyTranslationAndRotation(PoseStack pose) {
        Vector3f translation = getRealPosition();
        pose.translate(translation.x(), translation.y(), translation.z());

        Vector3f rotation = getRealRotation();
        if (rotation.equals(Rotationable.ZERO)) return;
        pose.mulPose(Axis.ZP.rotationDegrees(rotation.z()));
        pose.mulPose(Axis.YP.rotationDegrees(rotation.y()));
        pose.mulPose(Axis.XP.rotationDegrees(rotation.x()));

        if (scale.equals(Cube.BASE_SCALE)) return;
        pose.scale(scale.x(), scale.y(), scale.z());
    }

    private Vector3f getRealPosition() {
        Vector3f result = new Vector3f(this.position);
        result.add(this.offset);
        return result;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }

    private Vector3f getRealRotation() {
        Vector3f result = new Vector3f(this.rotation);
        result.add(this.animRot);
        return result;
    }

    public void setAnimRot(Vector3f animRot) {
        this.animRot = animRot;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public SimpleColor getColor() {
        return color;
    }

    public void setColor(SimpleColor color) {
        this.color = color;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void render(PoseStack pose, MultiBufferSource buffer, int light, int overlay) {
        pose.pushPose();
        applyTranslationAndRotation(pose);
        VertexConsumer consumer = buffer.getBuffer(renderType);
        roots.forEach(c -> c.render(pose, consumer, color, light, overlay));
        pose.popPose();
    }

    public void clearAnim() {
        this.initAnim();
        this.roots.forEach(b -> {
            if (!(b instanceof AnimBone animBone)) return;
            animBone.recursionClearAnim();
        });
    }

    public void applyAnimation(HashMap<String, Triple<Vector3f, Vector3f, Vector3f>> multipliers) {
        this.children.forEach((name, r) -> {
            if (!(r instanceof AnimBone animBone)) return;
            Triple<Vector3f, Vector3f, Vector3f> multiplier = multipliers.getOrDefault(name, null);
            if (multiplier == null) return;
            animBone.setOffset(multiplier.getLeft());
            animBone.setAnimRot(multiplier.getMiddle());
            animBone.setScale(multiplier.getRight());
        });
    }

    public AnimModel copy() {
        return new AnimModel(this.geometry, this.material, this.renderType);
    }
}
