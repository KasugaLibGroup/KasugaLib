package kasuga.lib.core.client.render.curve;

import kasuga.lib.core.client.render.texture.Vec2f;
import kasuga.lib.core.util.data_type.Pair;

import java.util.List;

public interface CurveTemplate {
    List<Vec2f> getPointList();
    Pair<Float, Float> getRange();

    default Float getLeft() {
        return getRange().getFirst();
    }

    default Float getRight() {
        return getRange().getSecond();
    }
    void setLeft(float left);
    void setRight(float right);

    float getStep();
}
