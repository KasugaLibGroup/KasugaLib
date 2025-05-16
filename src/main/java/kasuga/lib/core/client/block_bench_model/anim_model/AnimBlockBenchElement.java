package kasuga.lib.core.client.block_bench_model.anim_model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchElement;
import kasuga.lib.core.client.block_bench_model.model.BlockBenchGroup;
import kasuga.lib.core.client.block_bench_model.model.TransformContext;
import kasuga.lib.core.client.render.SimpleColor;
import lombok.Getter;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
@Getter
public class AnimBlockBenchElement extends AnimElement implements Renderable {

    private final BlockBenchElement element;

    private final HashMap<String, Vector3f> vertices;

    private final HashSet<BakedQuad> bakedQuads;

    @Getter
    private boolean baked;

    public AnimBlockBenchElement(AnimBlockBenchModel model,
                                 AnimBlockBenchGroup parent,
                                 BlockBenchElement element) {
        super(model, parent, element.getId());
        this.element = element;
        super.pivot = element.getPivot();
        vertices = new HashMap<>();
        this.element.getVertices().forEach(
                (name, vertex) -> {
                    vertices.put(name, new Vector3f(
                            vertex.x() / 16f,
                            vertex.y() / 16f,
                            vertex.z() / 16f
                    ));
                }
        );
        bakedQuads = new HashSet<>();
        baked = false;
    }

    public void bake() {
        TransformContext context = new TransformContext();
        bakedQuads.clear();
        element.getFaces().forEach(face -> {
            if (!face.getFace().hasTexture() || !face.isRender()) return;
            BakedQuad quad = face.fillVertices(vertices,
                    getModel().getModel().getResolution(),
                    Material::sprite,
                    context);
            bakedQuads.add(quad);
        });
        baked = true;
    }


    @Override
    public void render(PoseStack pose, VertexConsumer consumer, SimpleColor color, int light, int overlay) {
        if (!element.isRender()) return;
        if (!baked) {
            bake();
        }
        pose.pushPose();
        translateToPivot(pose);
        baseRotation(pose, this.element.getRotation());
        transform.transform(pose);
        for (BakedQuad quad : bakedQuads) {
            consumer.putBulkData(pose.last(), quad, color.getfR(),
                    color.getfG(), color.getfB(), light, overlay);
        }
        pose.popPose();
    }
}
