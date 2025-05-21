package kasuga.lib.core.client.block_bench_model.model;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.block_bench_model.json_data.Element;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.IModelBuilder;

import java.util.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
@Getter
public class BlockBenchElement implements ModelElement {

    private final Element element;

    private final String name;

    private final Vector3f pivot;

    private final Vector3f rotation;

    private final boolean render;

    private final UUID id;

    private final HashMap<String, Vector3f> vertices;
    private final List<BlockBenchFace> faces;

    public BlockBenchElement(Element element) {
        this.element = element;
        this.name = element.getName();
        this.pivot = element.getPivot();
        this.rotation = element.getRotation();
        this.render = element.isExport() && element.isVisibility();
        this.id = UUID.randomUUID();
        vertices = element.getVertices();
        faces = new ArrayList<>();
        element.getFaces().forEach((n, f) -> faces.add(new BlockBenchFace(f)));
    }

    public void addQuads(IModelBuilder<?> modelBuilder, ModelState modelTransform,
                         TransformContext transform, Vec2f resolution,
                         Function<Material, TextureAtlasSprite> spriteGetter) {
        if (!render) return;
        TransformContext myTransform = transform.transform(this.rotation, this.pivot);
        Transformation rotation = modelTransform.getRotation();
        Vector3f universalOffset = rotation.getTranslation();
        Vector3f universalScale = rotation.getScale();
        HashMap<String, Vector3f> transformedVertices = new HashMap<>();
        for (Map.Entry<String, Vector3f> entry : vertices.entrySet()) {
            Vector3f v = new Vector3f(
                    entry.getValue().x(),
                    entry.getValue().y(),
                    entry.getValue().z()
            );
            v = myTransform.applyToElement(v);
            v.mul(1/16f);
            v.add(universalOffset);
            v.add(TransformContext.BASE_OFFSET);
            v.mul(universalScale.x(), universalScale.y(), universalScale.z());
            transformedVertices.put(entry.getKey(), v);
        }
        for (BlockBenchFace face : faces) {
            BakedQuad quad = face.fillVertices(transformedVertices, resolution, spriteGetter, transform);
            if (quad != null) modelBuilder.addUnculledFace(quad);
        }
    }
}
