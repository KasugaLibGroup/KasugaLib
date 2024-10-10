package kasuga.lib.core.model.base;

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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Bone implements Rotationable {

    public final String name, parent;

    public final Vector3f pivot, rotation;
    public final ArrayList<Cube> cubes;

    public final Geometry model;

    public Bone(JsonObject jsonObject, Geometry model) {
        this.model = model;
        this.name = jsonObject.get("name").getAsString();
        this.parent = jsonObject.has("parent") ?
                jsonObject.get("parent").getAsString() : null;

        JsonArray pivotJson = jsonObject.getAsJsonArray("pivot");
        this.pivot = new Vector3f(
                pivotJson.get(0).getAsFloat(),
                pivotJson.get(1).getAsFloat(),
                pivotJson.get(2).getAsFloat()
                );

        if (jsonObject.has("rotation")) {
            JsonArray rotationJson = jsonObject.getAsJsonArray("rotation");
            rotation = new Vector3f(
                    rotationJson.get(0).getAsFloat(),
                    rotationJson.get(1).getAsFloat(),
                    rotationJson.get(2).getAsFloat()
                    );
        } else rotation = new Vector3f();


        cubes = new ArrayList<>();
        JsonArray cubesJson = jsonObject.getAsJsonArray("cubes");
        for (JsonElement cubeJson : cubesJson) {
            Cube cube = new Cube(cubeJson.getAsJsonObject(), model, this);
            this.cubes.add(cube);
        }
    }

    public void applyRotation(List<RotationInstruction> instructions) {
        if (this.hasParent()) {
            Bone parentBone = getParent();
            if (parentBone != null) parentBone.applyRotation(instructions);
        }
        if (!this.rotation.equals(Vector3f.ZERO)) {
            Vector3f p = this.pivot.copy(); p.mul(1 / 16f);
            instructions.add(new RotationInstruction(p, this.rotation));
        }
    }

    public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        cubes.forEach(cube -> cube.addQuads(
                owner, modelBuilder, bakery,
                spriteGetter, modelTransform, modelLocation
        ));
    }
    public boolean hasParent() {
        return parent != null;
    }

    public @Nullable Bone getParent() {
        if (parent == null) return null;
        return model.getBone(this.parent);
    }
}
