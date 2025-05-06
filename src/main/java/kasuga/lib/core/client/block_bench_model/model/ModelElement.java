package kasuga.lib.core.client.block_bench_model.model;

import com.mojang.math.Vector3f;
import kasuga.lib.core.client.render.texture.Vec2f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraftforge.client.model.IModelBuilder;

import java.util.UUID;
import java.util.function.Function;

public interface ModelElement {

    UUID getId();
    String getName();
    Vector3f getPivot();
    Vector3f getRotation();
    boolean isRender();

    default TransformContext transform(TransformContext fromParent) {
        return fromParent.transform(getRotation(), getPivot());
    }

    void addQuads(IModelBuilder<?> modelBuilder, ModelState modelTransform,
                  TransformContext transform, Vec2f resolution,
                  Function<Material, TextureAtlasSprite> spriteGetter);
}
