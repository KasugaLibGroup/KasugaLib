package kasuga.lib.core.model.base;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.model.BoxLayerProcessor;
import kasuga.lib.core.model.GeometryDescription;
import kasuga.lib.core.model.Rotationable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Cube implements Rotationable {
    private final Vector3f origin, size, pivot, rotation;
    private final ArrayList<UnbakedUV> uvs;

    public final Geometry model;

    public final Bone bone;
    public final boolean mirror, visible, emissive;

    public Cube(Vector3f org, Vector3f size, boolean mirror) {
        this.origin = org;
        this.size = size;
        this.pivot = new Vector3f();
        this.rotation = new Vector3f();
        this.uvs = new ArrayList<>();
        this.model = null;
        this.bone = null;
        this.mirror = mirror;
        this.visible = false;
        this.emissive = false;
    }

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

        if (object.has("pivot")) {
            JsonArray pivotJson = object.getAsJsonArray("pivot");
            pivot = new Vector3f(
                    pivotJson.get(0).getAsFloat(),
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

        // box layers
        if (object.get("uv").isJsonArray()) {
            JsonArray uv = object.getAsJsonArray("uv");
            Vec2f uvOffset = new Vec2f(uv.get(0).getAsFloat(), uv.get(1).getAsFloat())
                    .scale(1 / getDescription().getTextureWidth(), 1 / getDescription().getTextureHeight());
            BoxLayerProcessor processor = new BoxLayerProcessor(this, uvOffset);

            for (Direction value : Direction.values()) uvs.add(processor.getUV(value));
            return;
        }

        for (Map.Entry<String, JsonElement> entry : object.get("uv").getAsJsonObject().entrySet()) {
            UnbakedUV uv = new UnbakedUV(Direction.byName(entry.getKey()), entry.getValue().getAsJsonObject(),
                    model.getDescription().getTextureWidth(), model.getDescription().getTextureHeight(),
                    mirror, false, visible, emissive);
            this.uvs.add(uv);
        }
    }

    public GeometryDescription getDescription() {
        return model.getDescription();
    }

    public List<Quad> getQuads() {
        List<Quad> result = new ArrayList<>(6);
        uvs.forEach(uv -> result.add(new Quad(this, uv, model)));
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

    public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        ArrayList<RotationInstruction> instructions = Lists.newArrayList();
        applyRotation(instructions);
        getQuads().forEach(quad -> quad.addQuads(
                owner, modelBuilder, bakery,
                spriteGetter, modelTransform,
                modelLocation, instructions
        ));
    }

    @Override
    public void applyRotation(List<RotationInstruction> instructions) {
        bone.applyRotation(instructions);
        if (!this.rotation.equals(Vector3f.ZERO)) {
            Vector3f p = this.pivot.copy(); p.mul(1 / 16f);
            instructions.add(new RotationInstruction(p, this.rotation));
        }
    }
}
