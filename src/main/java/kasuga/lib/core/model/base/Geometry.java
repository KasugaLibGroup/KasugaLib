package kasuga.lib.core.model.base;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Vector3f;
import kasuga.lib.core.model.GeometryDescription;
import kasuga.lib.core.model.ItemTransformMapping;
import kasuga.lib.core.model.UnbakedBedrockModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Geometry {
    private final GeometryDescription description;
    private final HashMap<String, Bone> bones;
    private final UnbakedBedrockModel model;
    private final HashMap<String, ItemTransform> itemTransforms;

    public Geometry(JsonObject json, UnbakedBedrockModel model) {
        this.model = model;
        description = new GeometryDescription(json.getAsJsonObject("description"));
        bones = Maps.newHashMap();
        itemTransforms = Maps.newHashMap();
        parse(json);
    }

    public void parse(JsonObject json) {
        JsonArray bonesJson = json.getAsJsonArray("bones");
        for (JsonElement boneJson : bonesJson) {
            Bone bone = new Bone(boneJson.getAsJsonObject(), this);
            bones.put(bone.name, bone);
        }
        parseTransforms(json);
    }

    public static HashMap<ItemTransforms.TransformType, ItemTransform> parseTransforms(JsonObject json) {
        if (!json.has("item_display_transforms")) return Maps.newHashMap();
        JsonObject displayTransforms = json.getAsJsonObject("item_display_transforms");
        HashMap<ItemTransforms.TransformType, ItemTransform> result = Maps.newHashMap();
        for (Map.Entry<String, JsonElement> entry : displayTransforms.entrySet()) {
            String transName = entry.getKey();
            ItemTransforms.TransformType type = ItemTransformMapping.getType(transName);
            JsonObject transBody = entry.getValue().getAsJsonObject();
            Vector3f rotation = readVec3fFromJsonArray(transBody.getAsJsonArray("rotation"));
            Vector3f translation = readVec3fFromJsonArray(transBody.getAsJsonArray("translation"));
            Vector3f scale = readVec3fFromJsonArray(transBody.getAsJsonArray("scale"));
            Vector3f rotationPivot = readVec3fFromJsonArray(transBody.getAsJsonArray("rotation_pivot"));
            Vector3f scalePivot = readVec3fFromJsonArray(transBody.getAsJsonArray("scale_pivot"));
            ItemTransform transform = new ItemTransform(rotation, translation, scale);
            result.put(type, transform);
        }
        return result;
    }

    public static Vector3f readVec3fFromJsonArray(JsonArray array) {
        return new Vector3f(
                array.get(0).getAsFloat(),
                array.get(1).getAsFloat(),
                array.get(2).getAsFloat()
        );
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

    public UnbakedBedrockModel getModel() {
        return model;
    }
}
