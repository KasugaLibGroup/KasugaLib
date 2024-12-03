package kasuga.lib.core.client.model.anim_model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.model.NamedRenderTypeManager;
import kasuga.lib.core.client.model.model_json.Geometry;
import kasuga.lib.core.client.render.SimpleColor;
import kasuga.lib.core.client.model.BedrockRenderable;
import kasuga.lib.core.client.model.model_json.Cube;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class AnimModel implements IMultipartModelGeometry<AnimModel>, Animable {

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
        this.renderType = () -> NamedRenderTypeManager.get(renderTypeHint);
        position = Cube.BASE_OFFSET.copy();
        rotation = Vector3f.ZERO.copy();
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
        position = Cube.BASE_OFFSET.copy();
        rotation = Vector3f.ZERO.copy();
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
        position = Cube.BASE_OFFSET.copy();
        rotation = Vector3f.ZERO.copy();
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
        this.position = position.copy();
        this.rotation = rotation.copy();
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
        position = Vector3f.ZERO.copy();
        rotation = Vector3f.ZERO.copy();
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
        this.offset = Vector3f.ZERO.copy();
        this.animRot = Vector3f.ZERO.copy();
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
        if (rotation.equals(Vector3f.ZERO)) return;
        pose.mulPose(Vector3f.ZP.rotationDegrees(rotation.z()));
        pose.mulPose(Vector3f.YP.rotationDegrees(rotation.y()));
        pose.mulPose(Vector3f.XP.rotationDegrees(rotation.x()));

        if (scale.equals(Cube.BASE_SCALE)) return;
        pose.scale(scale.x(), scale.y(), scale.z());
    }

    private Vector3f getRealPosition() {
        Vector3f result = this.position.copy();
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
    public Collection<? extends IModelGeometryPart> getParts() {
        return this.geometry.getModel().getParts();
    }

    @Override
    public Optional<? extends IModelGeometryPart> getPart(String name) {
        return this.geometry.getModel().getPart(name);
    }

    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        geometry.addQuads(owner, modelBuilder, bakery, spriteGetter, modelTransform, modelLocation);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return geometry.getModel().getTextures(context, modelGetter, missingTextureErrors);
    }
}
