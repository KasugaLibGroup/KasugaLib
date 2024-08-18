package kasuga.lib.core.client.frontend.common.interaction;

public class PlaneMouseContext implements MouseContext{
    private final int x;
    private final int y;

    public PlaneMouseContext(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }
}
