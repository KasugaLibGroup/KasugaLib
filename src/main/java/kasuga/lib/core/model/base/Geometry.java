package kasuga.lib.core.model.base;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kasuga.lib.core.model.GeometryDescription;
import kasuga.lib.core.model.UnbakedAnimModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.HashMap;
import java.util.function.Function;

public class Geometry {
    private final GeometryDescription description;
    private final HashMap<String, Bone> bones;
    private final UnbakedAnimModel model;

    public Geometry(JsonObject json, UnbakedAnimModel model) {
        this.model = model;
        description = new GeometryDescription(json.getAsJsonObject("description"));
        bones = Maps.newHashMap();
        parse(json);
    }

    public void parse(JsonObject json) {
        JsonArray bonesJson = json.getAsJsonArray("bones");
        for (JsonElement boneJson : bonesJson) {
            Bone bone = new Bone(boneJson.getAsJsonObject(), this);
            bones.put(bone.name, bone);
        }
    }

    public GeometryDescription getDescription() {
        return description;
    }

    public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        bones.forEach((name, bone) -> bone.addQuads(
                owner, modelBuilder, bakery,
                spriteGetter, modelTransform, modelLocation
        ));
    }

    public Bone getBone(String name) {
        return bones.getOrDefault(name, null);
    }

    public UnbakedAnimModel getModel() {
        return model;
    }
}
