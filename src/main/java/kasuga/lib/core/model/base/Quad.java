package kasuga.lib.core.model.base;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.core.model.Rotationable;
import kasuga.lib.core.util.data_type.Pair;
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
    public static final Vector3f BASE_OFFSET = new Vector3f(.5f, 0, .5f);

    public Quad(Cube cube, UnbakedUV uv, Geometry model) {
        this.model = model;
        vertices = new Vertex[4];
        this.direction = uv.getDirection();
        for (int i = 0; i < 4; i++) vertices[i] = new Vertex(cube, uv, i);
        skip = (uv.getUvSize().x() == 0 || uv.getUvSize().y() == 0) || !uv.isVisible();
    }

    public Pair<Vector3f[], Quaternion> applyRotation(List<Rotationable.RotationInstruction> instructions) {
        Vector3f position = new Vector3f();
        Vector3f rotation = new Vector3f();
        Quaternion quaternion = null;
        Vector3f lastPivot = new Vector3f();
        for (Rotationable.RotationInstruction instruction : instructions) {
            Vector3f pivotOffset = instruction.pivot().copy();
            pivotOffset.sub(lastPivot);
            if (quaternion != null) pivotOffset.transform(quaternion);
            position.add(pivotOffset);

            float rz = instruction.rotation().z();
            float ry = instruction.rotation().y();
            float rx = - instruction.rotation().x();
            rotation.add(instruction.rotation());
            Vector3f v = new Vector3f(0, 0, rz);
            if (quaternion == null) {
                quaternion = Quaternion.fromXYZDegrees(v);
                v = new Vector3f(0, ry, 0);
                quaternion.mul(Quaternion.fromXYZDegrees(v));
                v = new Vector3f(rx, 0, 0);
                quaternion.mul(Quaternion.fromXYZDegrees(v));
            } else {
                quaternion.mul(Quaternion.fromXYZDegrees(v));
                v = new Vector3f(0, ry, 0);
                quaternion.mul(Quaternion.fromXYZDegrees(v));
                v = new Vector3f(rx, 0, 0);
                quaternion.mul(Quaternion.fromXYZDegrees(v));
            }


            // save data
            lastPivot = instruction.pivot();
        }
        rotation.mul(-1f, 1f, 1f);
        return Pair.of(new Vector3f[]{position, rotation, lastPivot},
                quaternion == null ? Quaternion.fromXYZDegrees(Vector3f.ZERO) : quaternion);
    }

    public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery,
                         Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                         ResourceLocation modelLocation, List<Rotationable.RotationInstruction> instructions) {
        if (skip) return;
        TextureAtlasSprite sprite = spriteGetter.apply(model.getModel().getMaterial());
        Vector3f universalOffset = modelTransform.getRotation().getTranslation();
        Quaternion universalRotation = modelTransform.getRotation().getLeftRotation();
        Vector3f scale = modelTransform.getRotation().getScale();

        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        float width = u1 - u0;
        float height = v1 - v0;

        Pair<Vector3f[], Quaternion> triple = applyRotation(instructions);
        Vector3f offset = triple.getFirst()[0].copy();
        if (universalOffset != null) {
            offset.add(universalOffset);
        }
        offset.sub(triple.getFirst()[2]);

        int[] aint = new int[32];
        for(int i = 0; i < 4; i++) {
            vertices[i]
                    .applyTranslation(offset)
                    .applyRotation(Vector3f.ZERO, universalRotation)
                    .applyRotation(triple.getFirst()[0], triple.getSecond())
                    .applyScale(Vector3f.ZERO, scale)
                    .applyTranslation(BASE_OFFSET)
                    .fillVertex(aint, i, u0, v0, width, height);
        }
        BakedQuad quad = new BakedQuad(aint, 0, direction, sprite, true);
        modelBuilder.addCulledFace(direction, quad);
    }
}
