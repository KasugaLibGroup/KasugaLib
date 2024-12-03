package kasuga.lib.core.client.model.anim_model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import kasuga.lib.core.client.model.Rotationable;
import kasuga.lib.core.client.model.model_json.Geometry;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.model.BedrockRenderable;
import kasuga.lib.core.client.model.model_json.Cube;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import org.joml.Vector3f;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class AnimModel extends SimpleUnbakedGeometry<AnimModel> implements Animable {

    public final Map<String, BedrockRenderable> children;
    public final List<BedrockRenderable> roots;
    public final Geometry geometry;
    public final List<Material> materials;
    public final Supplier<RenderType> renderType;
    private SimpleColor color;

    public Vector3f position, rotation,
                    offset, animRot, scale;
    @Getter
    private boolean validForRender;

    public AnimModel(Geometry geometry, List<Material> material, ResourceLocation renderTypeHint) {
        this.validForRender = false;
        this.geometry = geometry;
        this.children = Maps.newHashMap();
        this.materials = new ArrayList<>(material);
        this.renderType = () -> NamedRenderTypeManager.get(renderTypeHint).block();
        position = new Vector3f(Cube.BASE_OFFSET);
        rotation = new Vector3f();
        color = SimpleColor.fromRGBInt(0xffffff);
        roots = Lists.newArrayList();
        // collectChildren();
        // initAnim();
    }

    public AnimModel(Geometry geometry, List<Material> material, RenderType renderType) {
        this.validForRender = false;
        this.geometry = geometry;
        this.children = Maps.newHashMap();
        this.materials = new ArrayList<>(material);
        this.renderType = () -> renderType;
        position = new Vector3f(Cube.BASE_OFFSET);
        rotation = new Vector3f();
        color = SimpleColor.fromRGBInt(0xffffff);
        roots = Lists.newArrayList();
        // collectChildren();
        // initAnim();
    }

    public AnimModel(Geometry geometry, List<Material> material, Supplier<RenderType> renderType) {
        this.validForRender = false;
        this.geometry = geometry;
        this.children = Maps.newHashMap();
        this.materials = new ArrayList<>(material);
        this.renderType = renderType;
        position = new Vector3f(Cube.BASE_OFFSET);
        rotation = new Vector3f();
        color = SimpleColor.fromRGBInt(0xffffff);
        roots = Lists.newArrayList();
        // collectChildren();
        // initAnim();
    }

    protected AnimModel(Geometry geometry, List<Material> material, RenderType renderType,
                      Vector3f position, Vector3f rotation, SimpleColor color) {
        this.validForRender = false;
        this.geometry = geometry;
        this.materials = new ArrayList<>(material);
        this.renderType = () -> renderType;
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.color = color.copy();
        this.children = Maps.newHashMap();
        this.roots = Lists.newArrayList();
        // initAnim();
    }

    public AnimModel(Geometry geometry, List<Material> material, RenderType renderType,
                     Map<String, BedrockRenderable> children, List<BedrockRenderable> roots) {
        this.validForRender = false;
        this.geometry = geometry;
        this.children = children;
        this.materials = new ArrayList<>(material);
        this.renderType = () -> renderType;
        position = new Vector3f();
        rotation = new Vector3f();
        this.roots = roots;
        color = SimpleColor.fromRGBInt(0xffffff);
        // initAnim();
    }

    public void resetAnimation() {
        this.initAnim();
    }

    public void init() {
        collectChildren();
        initAnim();
        validForRender = true;
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


    public Map<String, BedrockRenderable> getChildren() {
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
        if (!validForRender) return;
        pose.pushPose();
        applyTranslationAndRotation(pose);
        VertexConsumer consumer = buffer.getBuffer(renderType.get());
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
        return new AnimModel(this.geometry, this.materials, this.renderType);
    }

    @Override
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        geometry.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
    }

    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return geometry.getModel().getMaterials(context, modelGetter, missingTextureErrors);
    }
}
