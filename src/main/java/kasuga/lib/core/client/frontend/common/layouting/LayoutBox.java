package kasuga.lib.core.client.frontend.common.layouting;

import java.util.Objects;

public class LayoutBox {
    public static final LayoutBox ZERO = LayoutBox.of(0, 0, 0, 0);
    public final float x;
    public final float y;
    public final float width;
    public final float height;

    public LayoutBox(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static LayoutBox of(float x, float y, float width, float height) {
        return new LayoutBox(x,y,width,height);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LayoutBox layoutBox = (LayoutBox) object;
        return x == layoutBox.x && y == layoutBox.y && width == layoutBox.width && height == layoutBox.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    public LayoutBox addCoordinateFrom(LayoutBox target){
        return new LayoutBox(target.x + x, target.y + y, width + target.width, height + target.height);
    }
}
