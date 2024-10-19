package kasuga.lib.core.client.model.model_json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.model.Rotationable;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class Bone implements Rotationable {

    public final String name, parent;

    public final Vector3f pivot, rotation;
    public final ArrayList<Cube> cubes;
    public final HashMap<String, Locator> locators;

    public final Geometry model;

    public Bone(JsonObject jsonObject, Geometry model) {
        this.model = model;
        this.name = jsonObject.get("name").getAsString();
        this.parent = jsonObject.has("parent") ?
                jsonObject.get("parent").getAsString() : null;

        JsonArray pivotJson = jsonObject.getAsJsonArray("pivot");
        this.pivot = new Vector3f(
                - pivotJson.get(0).getAsFloat(),
                pivotJson.get(1).getAsFloat(),
                pivotJson.get(2).getAsFloat()
                );

        if (jsonObject.has("rotation")) {
            JsonArray rotationJson = jsonObject.getAsJsonArray("rotation");
            rotation = Geometry.readVec3fFromJsonArray(rotationJson);
        } else rotation = new Vector3f();

        locators = new HashMap<>();
        if (jsonObject.has("locators")) {
            JsonObject locatorJson = jsonObject.getAsJsonObject("locators").getAsJsonObject();
            locatorJson.entrySet().forEach(entry -> {
                if (!entry.getValue().isJsonObject()) {
                    if (entry.getValue().isJsonArray()) {
                        Vector3f locatorPos = Geometry.readVec3fFromJsonArray(entry.getValue().getAsJsonArray());
                        locatorPos.mul(-1, 1, 1);
                        locators.put(entry.getKey(), new Locator(locatorPos, Vector3f.ZERO.copy()));
                    } else return;
                }
                JsonObject o = entry.getValue().getAsJsonObject();
                Vector3f locatorPos = o.has("offset") ?
                        Geometry.readVec3fFromJsonArray(o.get("offset").getAsJsonArray()) :
                        Vector3f.ZERO.copy();
                locatorPos.mul(-1, 1, 1);
                Vector3f locatorRot = o.has("rotation") ?
                        Geometry.readVec3fFromJsonArray(o.get("rotation").getAsJsonArray()) :
                        Vector3f.ZERO.copy();
                locators.put(entry.getKey(), new Locator(locatorPos, locatorRot));
            });
        }

        cubes = new ArrayList<>();
        if (!jsonObject.has("cubes")) return;
        JsonArray cubesJson = jsonObject.getAsJsonArray("cubes");
        for (JsonElement cubeJson : cubesJson) {
            Cube cube = new Cube(cubeJson.getAsJsonObject(), model, this);
            this.cubes.add(cube);
        }
    }

    public HashMap<String, Locator> getLocators() {
        return locators;
    }

    public ArrayList<Cube> getCubes() {
        return cubes;
    }

    @Override
    public Vector3f getPivot() {
        return pivot;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
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

    public boolean isFlipV() {
        return model.isFlipV();
    }
}
