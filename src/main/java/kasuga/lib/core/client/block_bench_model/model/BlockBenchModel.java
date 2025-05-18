package kasuga.lib.core.client.block_bench_model.model;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import kasuga.lib.KasugaLib;
import kasuga.lib.core.KasugaLibClient;
import kasuga.lib.core.client.block_bench_model.json_data.*;
import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@Getter
public class BlockBenchModel extends SimpleUnbakedGeometry<BlockBenchModel> {

    private final BlockBenchFile file;
    private final HashMap<Texture, Pair<ResourceLocation, Material>> materials;
    private final BlockBenchGroup rootGroup;
    private final boolean isSingleTexture;
    private final HashMap<UUID, BlockBenchElement> elements;
    private final Vec2f resolution;

    public BlockBenchModel(BlockBenchFile file) throws IOException {
        this.file = file;
        materials = new HashMap<>();
        elements = new HashMap<>();
        resolution = file.getResolution();
        collectMaterials();
        isSingleTexture = materials.size() == 1;

        rootGroup = new BlockBenchGroup();
        buildModelFromFile();
        bindAllMaterials();
    }

    public void bindAllMaterials() {
        elements.forEach((id, ele) -> {
            ele.getFaces().forEach(face -> {
                Optional<Integer> indexOpt = face.getTextureIndex();
                if (indexOpt.isEmpty()) {
                    face.setRender(false);
                    return;
                }
                Integer index = indexOpt.get();
                Material mapped = null;
                Texture tex = null;
                for (Texture texture : materials.keySet()) {
                    if (index.equals(texture.getArrayIndex())) {
                        mapped = materials.get(texture).getSecond();
                        tex = texture;
                        if (!isSingleTexture) {
                            tex.setScaleFactor(
                                    new Vec2f(
                                            tex.getUvWidth() / resolution.x(),
                                            tex.getUvHeight() / resolution.y()
                                    )
                            );
                        }
                        break;
                    }
                }
                if (mapped == null) {
                    face.setRender(false);
                    return;
                }
                face.mapTexture(tex, mapped);
            });
        });
    }

    public void buildModelFromFile() {
        Outline outline = file.getOutline();
        if (outline == null) {
            return;
        }
        fillGroup(rootGroup, outline.getElements());
    }

    public void fillGroup(BlockBenchGroup group, HashMap<UUID, IElement> elements) {
        HashMap<UUID, ModelElement> children = group.getChildren();
        for (Map.Entry<UUID, IElement> entry : elements.entrySet()) {
            UUID id = entry.getKey();
            IElement element = entry.getValue();
            if (element instanceof Element e) {
                BlockBenchElement elem = new BlockBenchElement(e);
                children.put(id, elem);
                this.elements.put(id, elem);
            } else if (element instanceof Group g) {
                BlockBenchGroup grp = new BlockBenchGroup(g);
                children.put(id, grp);
                if (g.getChildren().isEmpty()) continue;
                fillGroup(grp, g.getChildren());
            }
        }
    }

    public void collectMaterials() throws IOException {
        List<Texture> textures = file.getTextures();
        for (Texture texture : textures) {
            if (texture.getSource() != null) {
                ResourceLocation location = getMaterialLocation(texture);
                Material material = texture.getSource().getAsMaterial(
                        TextureAtlas.LOCATION_BLOCKS, location
                );
                if (material == null) continue;
                materials.put(texture, Pair.of(location, material));
            } else {
                ResourceLocation location = new ResourceLocation(
                        texture.getNamespace(), texture.getPath()
                );
                Material material = new Material(TextureAtlas.LOCATION_BLOCKS, location);
                materials.put(texture, Pair.of(location, material));
            }
        }
    }

    public ResourceLocation getMaterialLocation(Texture texture) {
        String textName = texture.getName();
        while (textName.contains(".")) {
            textName = textName.substring(0, textName.indexOf("."));
        }
        textName = textName.toLowerCase();
        return new ResourceLocation(KasugaLib.MOD_ID,
                file.getName() + "/internal_textures/" + textName);
    }

    @Override
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder,
                            ModelBakery bakery,
                            Function<Material, TextureAtlasSprite> spriteGetter,
                            ModelState modelTransform, ResourceLocation modelLocation) {
        TransformContext context = new TransformContext();
        context.getQuaternion().mul(modelTransform.getRotation().getLeftRotation());
        rootGroup.addQuads(modelBuilder, modelTransform, context, resolution, spriteGetter);
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
        List<Material> upload = new ArrayList<>(materials.size());
        materials.forEach((tex, pair) -> upload.add(pair.getSecond()));
        return upload;
    }
}
