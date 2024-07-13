package kasuga.lib.core.client.frontend.gui.layout;

import kasuga.lib.core.client.frontend.gui.layout.yoga.api.YogaEdge;

import java.util.function.Function;

public class EdgeSize2D {

    public static final EdgeSize2D ZERO = new EdgeSize2D(0,0,0,0);

    public final float top;
    public final float right;
    public final float bottom;
    public final float left;

    public EdgeSize2D(float top, float right, float bottom, float left){
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public static EdgeSize2D of(float top, float right, float bottom, float left) {
        return new EdgeSize2D(top,right,bottom,left);
    }

    public static EdgeSize2D fromLambda(Function<YogaEdge,Float> lambda){
        return new EdgeSize2D(
                lambda.apply(YogaEdge.TOP),
                lambda.apply(YogaEdge.RIGHT),
                lambda.apply(YogaEdge.BOTTOM),
                lambda.apply(YogaEdge.LEFT)
        );
    }

    public float getTop() {
        return top;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public float getLeft() {
        return left;
    }
}
