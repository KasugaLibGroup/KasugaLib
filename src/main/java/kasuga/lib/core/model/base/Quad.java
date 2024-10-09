package kasuga.lib.core.model.base;

import com.mojang.math.Vector3f;
import kasuga.lib.core.model.Rotationable;
import kasuga.lib.core.model.UnbakedAnimModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.List;
import java.util.function.Function;

public class Quad {
    public final Vertex[] vertices;

    public final Direction direction;
    public final Geometry model;
    public final boolean skip;

    public Quad(Cube cube, UnbakedUV uv, Geometry model) {
        this.model = model;
        vertices = new Vertex[4];
        this.direction = uv.getDirection();
        for (int i = 0; i < 4; i++) vertices[i] = new Vertex(cube, uv, i);
        skip = uv.getUvSize().x() == 0 || uv.getUvSize().y() == 0;
    }

    public void applyRotation(List<Rotationable.RotationInstruction> instructions) {
        Vector3f axis = new Vector3f(1, 1, 1);
        Vector3f offset = new Vector3f();
        Vector3f rotation = new Vector3f();
        for (Rotationable.RotationInstruction instruction : instructions) {

        }
    }

    public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        if (skip) return;
        TextureAtlasSprite sprite = spriteGetter.apply(model.getModel().getMaterial());

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float width = u1 - u0;
        float height = v1 - v0;

        int[] aint = new int[32];
        for(int i = 0; i < 4; i++) {
            vertices[i].fillVertex(aint, i, u0, v0, width, height);
        }
        BakedQuad quad = new BakedQuad(aint, 0, direction, sprite, true);
        modelBuilder.addCulledFace(direction, quad);
    }
}
