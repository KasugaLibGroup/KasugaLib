package kasuga.lib.core.client.frontend.gui.canvas;

public record Point2D(float x, float y) {

    public static Point2D ZERO = new Point2D(0, 0);

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Point2D point2D = (Point2D) object;
        return Float.compare(x, point2D.x) == 0 && Float.compare(y, point2D.y) == 0;
    }

}
