package kasuga.lib.core.util.data_type;

import kasuga.lib.core.client.render.texture.Vec2f;

public class Vec2i {

    public int x;
    public int y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vec2i(Vec2i vec) {
        this.x = vec.x;
        this.y = vec.y;
    }

    public Vec2i(Vec2f vec2f) {
        this.x = (int) vec2f.x();
        this.y = (int) vec2f.y();
    }

    public Vec2i() {
        this(0, 0);
    }
}
