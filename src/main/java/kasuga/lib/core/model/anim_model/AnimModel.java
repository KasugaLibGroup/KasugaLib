package kasuga.lib.core.model.anim_model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.model.BedrockRenderable;
import kasuga.lib.core.model.base.Geometry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.Material;

import java.util.List;
import java.util.Map;

public class AnimModel {

    public final Map<String, BedrockRenderable> children;
    public final List<BedrockRenderable> roots;
    public final Geometry geometry;
    public final Material material;
    public final RenderType renderType;
    private SimpleColor color;

    public Vector3f position, rotation;

    public AnimModel(Geometry geometry, Material material, RenderType renderType) {
        this.geometry = geometry;
        this.children = Maps.newHashMap();
        this.material = material;
        this.renderType = renderType;
        position = Vector3f.ZERO.copy();
        rotation = Vector3f.ZERO.copy();
        color = SimpleColor.fromRGBInt(0xffffff);
        roots = Lists.newArrayList();
        collectChildren();
    }

    public AnimModel(Geometry geometry, Material material, RenderType renderType,
                     Map<String, BedrockRenderable> children, List<BedrockRenderable> roots) {
        this.geometry = geometry;
        this.children = children;
        this.material = material;
        this.renderType = renderType;
        position = Vector3f.ZERO.copy();
        rotation = Vector3f.ZERO.copy();
        this.roots = roots;
        color = SimpleColor.fromRGBInt(0xffffff);
    }

    public void collectChildren() {
        geometry.getBones().forEach((name, bone) -> {
            AnimBone ab = new AnimBone(bone, this);
            children.put(name, ab);
        });
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
        Vector3f translation = this.position;
        pose.translate(translation.x(), translation.y(), translation.z());

        Vector3f rotation = this.rotation;
        if (rotation.equals(Vector3f.ZERO)) return;
        pose.mulPose(Vector3f.ZP.rotationDegrees(rotation.z()));
        pose.mulPose(Vector3f.YP.rotationDegrees(rotation.y()));
        pose.mulPose(Vector3f.XP.rotationDegrees(rotation.x()));
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
}
