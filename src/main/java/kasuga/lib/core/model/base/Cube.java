package kasuga.lib.core.model.base;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.model.Rotationable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
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

        for (Map.Entry<String, JsonElement> entry : object.get("uv").getAsJsonObject().entrySet()) {
            UnbakedUV uv = new UnbakedUV(entry.getKey(), entry.getValue().getAsJsonObject(),
                    model.getDescription().getTextureWidth(), model.getDescription().getTextureHeight());
            this.uvs.add(uv);
        }
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
                spriteGetter, modelTransform, modelLocation
        ));
    }

    @Override
    public void applyRotation(List<RotationInstruction> instructions) {
        bone.applyRotation(instructions);
        if (!this.rotation.equals(Vector3f.ZERO))
            instructions.add(new RotationInstruction(this.pivot, this.rotation));
    }
}
