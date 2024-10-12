package kasuga.lib.core.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.model.base.Geometry;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class UnbakedBedrockModel extends SimpleUnbakedGeometry<UnbakedBedrockModel> {
    public final ResourceLocation modelLocation, textureLocation;
    private Material material;
    private ArrayList<Geometry> geometries;
    private final boolean flipV;
    private String formatVersion;
    public UnbakedBedrockModel(ResourceLocation modelLocation, ResourceLocation textureLocation, boolean flipV) {
        this.flipV = flipV;
        this.modelLocation = modelLocation;
        this.textureLocation = textureLocation;
        geometries = Lists.newArrayList();
        parse();
    }

    public void parse() {
        JsonObject model = readModel();
        if (model == null) {
            KasugaLib.MAIN_LOGGER.warn("Unable to open animated model: " + this.modelLocation.toString());
            return;
        }
        formatVersion = model.get("format_version").getAsString();
        this.material = new Material(TextureAtlas.LOCATION_BLOCKS, textureLocation);

        JsonArray geos = model.getAsJsonArray("minecraft:geometry");
        for (JsonElement element : geos) {
            JsonObject geometryJson = element.getAsJsonObject();
            Geometry geometry = new Geometry(geometryJson, this);
            geometries.add(geometry);
        }
    }

    @Override
    public Set<String> getConfigurableComponentNames() {
        Set<String> result = new HashSet<>();
        geometries.forEach(bone -> result.add(bone.getDescription().getIdentifier()));
        return result;
    }

    public JsonObject readModel() {
        JsonObject model;
        try {
            Resource resource = Resources.getResource(modelLocation);
            model = JsonParser.parseReader(resource.openAsReader()).getAsJsonObject();
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to load animated model: " + this.modelLocation.toString(), e);
            return null;
        }
        return model;
    }

    public String getFormatVersion() {
        return formatVersion;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isFlipV() {
        return flipV;
    }

    public List<Geometry> getGeometries() {
        return geometries;
    }

    @Override
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        geometries.forEach(geometry -> geometry.addQuads(
                owner, modelBuilder, bakery,
                spriteGetter, modelTransform, modelLocation
        ));
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> materials = new HashSet<>();
        materials.add(this.material);
        return materials;
    }
}
