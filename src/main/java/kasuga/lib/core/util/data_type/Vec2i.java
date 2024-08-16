package kasuga.lib.core.util.data_type;

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

    public Vec2i() {
        this(0, 0);
    }
}
