package kasuga.lib.core.client.animation.neo_neo.base;

import kasuga.lib.core.util.data_type.Pair;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;

public interface IBezier {

    List<Vec3> getBezierPoints();
    Vec3 getMovementVector();

    void createDefaultBezier(IBezier before);

    default void addBezierPoint(Vec3 point) {
        getBezierPoints().add(point);
    }

    default void addBezierPoint(int index, Vec3 point) {
        getBezierPoints().add(point);
    }
    default int bezierPointCount() {
        return getBezierPoints().size();
    }

    default boolean hasBezierPoints() {
        return !getBezierPoints().isEmpty();
    }

    default Pair<Vec3, Vec3> firstAndLast() {
        if (getBezierPoints().isEmpty()) return Pair.of(Vec3.ZERO, Vec3.ZERO);
        if (getBezierPoints().size() == 1) return Pair.of(getBezierPoints().get(0), getBezierPoints().get(0));
        return Pair.of(getBezierPoints().get(0), getBezierPoints().get(bezierPointCount() - 1));
    }
    default Vec3 beginDirection() {
        if (!hasBezierPoints()) return getMovementVector().scale(1 / getMovementVector().length());
        Pair<Vec3, Vec3> result = firstAndLast();
        Vec3 vec3 = result.getFirst().subtract(Vec3.ZERO);
        return vec3.scale(1 / vec3.length());
    }

    default Vec3 endDirection() {
        if (!hasBezierPoints()) return getMovementVector().scale(1 / getMovementVector().length());
        Pair<Vec3, Vec3> result = firstAndLast();
        Vec3 vec3 = getMovementVector().subtract(result.getSecond());
        return vec3.scale(1 / vec3.length());
    }

    default boolean removeBezierPoint(int index) {
        if (index < 0 || index > bezierPointCount()) return false;
        getBezierPoints().remove(index);
        return true;
    }

    default boolean removeBezierPoint(Vec3 vec3) {
        return getBezierPoints().remove(vec3);
    }

    default boolean containsPoint(Vec3 vec3) {
        return getBezierPoints().contains(vec3);
    }

    default void clearBezierPoints() {
        getBezierPoints().clear();
    }
}
