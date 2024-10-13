package kasuga.lib.core.model.base;

import com.mojang.math.Vector3f;
import net.minecraft.core.Direction;

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
        skip = (uv.getUvSize().x() == 0 || uv.getUvSize().y() == 0) || !uv.isVisible();
    }

    public Quad(Vertex[] vertices, Direction direction, Geometry model, boolean skip) {
        this.vertices = vertices;
        this.direction = direction;
        this.model = model;
        this.skip = skip;
    }

    public void fillVertex(int[] aint, float u0, float v0, float uWidth, float vHeight) {
        for (int i = 0; i < 4; i++) {
            vertices[i].fillVertex(aint, i, u0, v0, uWidth, vHeight);
        }
    }

    @ForAnimModel
    public void offsetWithoutCopy(Vector3f offset) {
        for (int i = 0; i < 4; i++) {
            vertices[i].position.add(offset);
        }
    }
}
