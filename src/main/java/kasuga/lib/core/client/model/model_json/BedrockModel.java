package kasuga.lib.core.client.model.model_json;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.util.Resources;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class BedrockModel extends SimpleUnbakedGeometry<BedrockModel> {
    public final ResourceLocation modelLocation;
    private final List<Material> materials;

    @Getter
    @Setter
    private Material textureMaterial;
    private final ArrayList<Geometry> geometries;
    private final boolean flipV;
    private String formatVersion;
    private boolean legacy;


    public BedrockModel(ResourceLocation modelLocation, boolean flipV) {
        this.flipV = flipV;
        this.modelLocation = modelLocation;
        geometries = Lists.newArrayList();
        materials = Lists.newArrayList();
        legacy = false;
        parse();
    }

    public BedrockModel(ResourceLocation modelLocation, boolean flipV, Material textureMaterial, List<Material> materials) {
        this(modelLocation, flipV, textureMaterial, materials.toArray(new Material[0]));
    }

    public BedrockModel(ResourceLocation modelLocation, boolean flipV, Material textureMaterial, @Nonnull Material... material) {
        this.flipV = flipV;
        this.modelLocation = modelLocation;
        this.textureMaterial = textureMaterial;
        this.geometries = new ArrayList<>();
        legacy = false;
        materials = Lists.newArrayList();
        parse();
        materials.addAll(List.of(material));
    }

    public void parse() {
        JsonObject model = readModel();
        if (model == null) {
            KasugaLib.MAIN_LOGGER.warn("Unable to open animated model: " + this.modelLocation.toString());
            return;
        }
        formatVersion = model.get("format_version").getAsString();


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

    public List<Material> getMaterials() {
        return materials;
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
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        geometries.forEach(geometry -> geometry.addQuads(
                owner, modelBuilder, baker,
                spriteGetter, modelTransform, modelLocation
        ));
    }

    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> materials = new HashSet<>(this.materials);
        return materials;
    }
}
