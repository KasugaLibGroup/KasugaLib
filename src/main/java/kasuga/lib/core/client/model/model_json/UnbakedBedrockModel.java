package kasuga.lib.core.client.model.model_json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.util.Pair;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.Resources;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class UnbakedBedrockModel implements IMultipartModelGeometry<UnbakedBedrockModel> {
    public final ResourceLocation modelLocation, textureLocation;
    private Material material;
    private ArrayList<Geometry> geometries;
    private final boolean flipV;
    private String formatVersion;
    private boolean legacy;


    public UnbakedBedrockModel(ResourceLocation modelLocation, ResourceLocation textureLocation, boolean flipV) {
        this.flipV = flipV;
        this.modelLocation = modelLocation;
        this.textureLocation = textureLocation;
        geometries = Lists.newArrayList();
        legacy = false;
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

        JsonArray geos;
        if (model.has("minecraft:geometry")) {
            geos = model.getAsJsonArray("minecraft:geometry");
            legacy = false;
        } else {
            geos = new JsonArray();
            if (model.has("geometry.model")) geos.add(model.get("geometry.model"));
            legacy = true;
        }
        for (JsonElement element : geos) {
            JsonObject geometryJson = element.getAsJsonObject();
            Geometry geometry = new Geometry(geometryJson, this);
            geometries.add(geometry);
        }
    }

    public JsonObject readModel() {
        JsonObject model;
        try {
            Resource resource = Resources.getResource(modelLocation);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            model = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
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

    public boolean isLegacy() {
        return legacy;
    }

    @Override
    public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        geometries.forEach(geometry -> geometry.addQuads(
                owner, modelBuilder, bakery,
                spriteGetter, modelTransform, modelLocation
        ));
    }

    @Override
    public Collection<? extends IModelGeometryPart> getParts() {
        return this.geometries;
    }

    @Override
    public Optional<? extends IModelGeometryPart> getPart(String name) {
        for (Geometry geometry : geometries) {
            if (geometry.getDescription().getIdentifier().equals(name)) {
                return Optional.of(geometry);
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> materials = new HashSet<>();
        materials.add(this.material);
        return materials;
    }
}
