package kasuga.lib.core.client.frontend.common.layouting;

import java.util.Objects;

public class LayoutBoxI {
    public static final LayoutBoxI ZERO = LayoutBoxI.of(0, 0, 0, 0);
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public LayoutBoxI(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static LayoutBoxI of(int x, int y, int width, int height) {
        return new LayoutBoxI(x,y,width,height);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LayoutBoxI layoutBox = (LayoutBoxI) object;
        return x == layoutBox.x && y == layoutBox.y && width == layoutBox.width && height == layoutBox.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    public LayoutBoxI addCoordinateFrom(LayoutBoxI target){
        return new LayoutBoxI(target.x + x, target.y + y, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
