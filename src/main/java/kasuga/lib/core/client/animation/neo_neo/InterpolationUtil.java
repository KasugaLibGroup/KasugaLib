package kasuga.lib.core.client.animation.neo_neo;

import interpreter.compute.data.Namespace;
import interpreter.compute.infrastructure.Formula;
import kasuga.lib.core.client.animation.neo_neo.base.IBezier;
import kasuga.lib.core.client.render.RendererUtil;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class InterpolationUtil {

    public static Vec3 linear(Vec3 move, float percentage) {
        return move.scale(percentage);
    }

    public static Vec3 bezier(Vec3 starter, Vec3 end, List<Vec3> bezierPoints, float percentage) {
        Queue<Vec3> points = new LinkedList<>();
        points.add(starter);
        points.addAll(bezierPoints);
        points.add(end);
        int counter = 0, len = points.size();
        Vec3 cache = null;
        while (len > 2) {
            if (counter == 0) {
                cache = points.poll();
                counter++;
                continue;
            }
            counter++;
            if (counter >= len) {
                counter = 0;
                len--;
                continue;
            }
            Vec3 vec = points.poll();
            points.add(vec.subtract(cache).scale(percentage).add(cache));
        }
        cache = points.poll();
        return points.poll().subtract(cache).scale(percentage).add(cache);
    }

    public static Vec3 custom(Namespace namespace, Formula x, Formula y, Formula z, float time) {
        namespace.assign("t", time);
        return new Vec3(x.getResult(), y.getResult(), z.getResult());
    }

    public static void createDefaultBezier(IBezier before, IBezier current, float factor) {
        Vec3 direction = before.endDirection();
        Vec3 endDirection = VectorUtil.mirror(current.getMovementVector(), direction);
        float len = (float) current.getMovementVector().length() * factor;
        current.addBezierPoint(direction.scale(len));
        current.addBezierPoint(current.getMovementVector().add(endDirection.reverse().scale(len)));
    }
}
