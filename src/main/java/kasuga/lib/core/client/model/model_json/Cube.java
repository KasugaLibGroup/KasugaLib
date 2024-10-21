package kasuga.lib.core.client.model.model_json;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.core.client.model.Rotationable;
import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class Cube implements Rotationable {
    private final Vector3f origin, size, pivot, rotation;
    private final ArrayList<UnbakedUV> uvs;

    public final Geometry model;

    public final Bone bone;
    public final boolean mirror, visible, emissive;
    private final float inflate;
    public static final Vector3f BASE_OFFSET = new Vector3f(.5f, 0, .5f),
                                BASE_SCALE = new Vector3f(1, 1, 1);

    public Cube(JsonObject object, Geometry model, Bone bone) {
        this.bone = bone;
        this.model = model;
        uvs = new ArrayList<>();
        JsonArray orgJson = object.getAsJsonArray("origin");
        origin = new Vector3f(
                orgJson.get(0).getAsFloat() / 16f,
                orgJson.get(1).getAsFloat() / 16f,
                orgJson.get(2).getAsFloat() / 16f
        );

        JsonArray sizeJson = object.getAsJsonArray("size");
        size = new Vector3f(
                sizeJson.get(0).getAsFloat() / 16f,
                sizeJson.get(1).getAsFloat() / 16f,
                sizeJson.get(2).getAsFloat() / 16f
        );
        origin.x = (-1 * (origin.x() + size.x()));

        if (object.has("pivot")) {
            JsonArray pivotJson = object.getAsJsonArray("pivot");
            pivot = new Vector3f(
                    - pivotJson.get(0).getAsFloat(),
                    pivotJson.get(1).getAsFloat(),
                    pivotJson.get(2).getAsFloat()
            );
        } else pivot = new Vector3f();

        if (object.has("rotation")) {
            JsonArray rotationJson = object.getAsJsonArray("rotation");
            rotation = new Vector3f(
                    rotationJson.get(0).getAsFloat(),
                    rotationJson.get(1).getAsFloat(),
                    rotationJson.get(2).getAsFloat()
            );
        } else rotation = new Vector3f();
        mirror = object.has("mirror") && object.get("mirror").getAsBoolean();
        visible = !object.has("visible") || object.get("visible").getAsBoolean();
        emissive = object.has("emissive") && object.get("emissive").getAsBoolean();

        inflate = object.has("inflate") ? object.get("inflate").getAsFloat() / 16f : 0f;

        boolean flipV = bone.isFlipV();
        // box layers
        if (object.get("uv").isJsonArray()) {
            JsonArray uv = object.getAsJsonArray("uv");
            Vec2f uvOffset = new Vec2f(uv.get(0).getAsFloat(), uv.get(1).getAsFloat())
                    .scale(1 / getDescription().getTextureWidth(), 1 / getDescription().getTextureHeight());
            BoxLayerProcessor processor = new BoxLayerProcessor(this, uvOffset, flipV);

            for (Direction value : Direction.values()) uvs.add(processor.getUV(value));
            return;
        }

        for (Map.Entry<String, JsonElement> entry : object.get("uv").getAsJsonObject().entrySet()) {
            UnbakedUV uv = new UnbakedUV(Direction.byName(entry.getKey()), entry.getValue().getAsJsonObject(),
                    model.getDescription().getTextureWidth(), model.getDescription().getTextureHeight(),
                    mirror, flipV, visible, emissive);
            this.uvs.add(uv);
        }
    }

    public GeometryDescription getDescription() {
        return model.getDescription();
    }

    public List<Quad> getQuads() {
        List<Quad> result = new ArrayList<>(6);
        for (UnbakedUV uv : uvs) {
            Quad quad = new Quad(this, uv, model);
            if (quad.skip) continue;
            result.add(quad);
        }
        return result;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public Vector3f getSize() {
        return size;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public boolean hasParent() {
        return true;
    }

    @Override
    public Rotationable getParent() {
        return bone;
    }

    @Override
    public Vector3f getPivot() {
        return pivot;
    }

    public float getInflate() {
        return inflate;
    }

    public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {

        TextureAtlasSprite sprite = spriteGetter.apply(model.getModel().getMaterial());
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float width = u1 - u0;
        float height = v1 - v0;

        RotationContext context = startCompileRotate();
        Vector3f position = context.position();
        position.mul(1 / 16f);

        Vector3f pivot = new Vector3f(getPivot());
        pivot.mul(1 / 16f);
        List<Quad> quads = getQuads();
        Vector3f universalOffset = new Vector3f(modelTransform.getRotation().getTranslation());
        Vector3f universalScale = modelTransform.getRotation().getScale();
        universalOffset.add(BASE_OFFSET);
        quads = rotQuads(quads, pivot, position, context.quaternions());
        if (!modelTransform.getRotation().getLeftRotation().equals(new Quaternionf()))
            quads = rotQuads(quads, Rotationable.ZERO, modelTransform.getRotation().getLeftRotation());
        if (!universalOffset.equals(BASE_OFFSET))
            quads = translateQuads(quads, universalOffset);
        if (!universalScale.equals(BASE_SCALE))
            quads = scaleQuads(quads, Rotationable.ZERO, universalScale);
        HashMap<Direction, int[]> aint = fillVertices(quads, u0, v0, width, height);
        aint.forEach(((direction, ints) -> {
            BakedQuad quad = new BakedQuad(ints, 0, direction, sprite, true);
            modelBuilder.addCulledFace(direction, quad);
        }));
    }

    public List<Quad> translateQuads(List<Quad> quads, Vector3f offset) {
        return controlQuads(quads, vertex -> vertex.applyTranslation(offset));
    }

    public List<Quad> rotQuads(List<Quad> input, Vector3f pivot, Vector3f position, List<Quaternionf> quaternions) {
        final Vector3f p = new Vector3f(pivot);
        return controlQuads(input, vertex -> vertex.applyRotation(p, position, quaternions));
    }

    public List<Quad> rotQuads(List<Quad> input, Vector3f pivot, Quaternionf quaternion) {
        final Vector3f p = new Vector3f(pivot);
        return controlQuads(input, vertex -> vertex.applyRotation(p, quaternion));
    }

    public List<Quad> scaleQuads(List<Quad> input, Vector3f pivot, Vector3f scale) {
        Vector3f s = new Vector3f(scale);
        return controlQuads(input, vertex -> vertex.applyScale(pivot, s));
    }

    public List<Quad> controlQuads(List<Quad> input, Function<Vertex, Vertex> function) {
        List<Quad> result = new ArrayList<>(input.size());
        for (Quad q : input) {
            Vertex[] vertices = new Vertex[4];
            for (int i = 0; i < 4; i++) {
                Vertex vertex = q.vertices[i];
                vertices[i] = function.apply(vertex);
            }
            result.add(new Quad(vertices, q.direction, q.model, q.skip));
        }
        return result;
    }

    public HashMap<Direction, int[]> fillVertices(List<Quad> quads, float u0, float v0, float uWidth, float vHeight) {
        HashMap<Direction, int[]> result = Maps.newHashMap();
        for (Quad quad : quads) {
            int[] v = new int[32];
            quad.fillVertex(v, u0, v0, uWidth, vHeight);
            result.put(quad.direction, v);
        }
        return result;
    }

    @ForAnimModel
    public List<BakedQuad> getBaked(TextureAtlasSprite sprite, Vector3f offset) {
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float width = u1 - u0;
        float height = v1 - v0;

        List<Quad> quads = getQuads();
        quads.forEach(quad -> quad.offsetWithoutCopy(offset));
        ArrayList<BakedQuad> result = new ArrayList<>(quads.size());

        HashMap<Direction, int[]> aint = fillVertices(quads, u0, v0, width, height);
        aint.forEach(((direction, ints) -> {
            BakedQuad baked = new BakedQuad(ints, 0, direction, sprite, true);
            result.add(baked);
        }));
        return result;
    }
}
