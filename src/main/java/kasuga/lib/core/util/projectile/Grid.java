package kasuga.lib.core.util.projectile;

import com.mojang.math.Vector3f;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.function.Function;

@Getter
public class Grid {

    private final Panel panel;

    private final Vector3f o, xAxis, yAxis;

    private final Vec2f xAxis2d, yAxis2d;

    public Grid(final Panel panel, Vector3f o, final Vec2f xAxis, final Vec2f yAxis) {
        this.panel = panel;
        this.o = o;
        this.xAxis2d = xAxis;
        this.yAxis2d = yAxis;
        this.xAxis = panel.map(xAxis);
        this.yAxis = panel.map(yAxis);
    }

    private Grid(final Panel panel, Vector3f o, Vector3f xAxis, Vector3f yAxis, Vec2f xAxis2d, Vec2f yAxis2d) {
        this.panel = panel;
        this.o = o;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.xAxis2d = xAxis2d;
        this.yAxis2d = yAxis2d;
    }

    public Grid copy() {
        return new Grid(this.panel, this.o, this.xAxis, this.yAxis, xAxis2d, yAxis2d);
    }

    public Vector3f get(Vec2f vec) {
        return get(vec.x(), vec.y());
    }

    public Vector3f get(float x, float y) {
        Vector3f result = o.copy();
        Vector3f x3d = xAxis.copy();
        x3d.mul(x);
        result.add(x3d);
        Vector3f y3d = yAxis.copy();
        y3d.mul(y);
        result.add(y3d);
        return result;
    }

    public Vec2f get(Vector3f vec) {
        float pi = (float) Math.PI;
        Vector3f offset = vec.copy();
        offset.sub(o);
        Vec2f offset2d = panel.map(offset);
        Function<Float, Float> mapper =
                angle -> angle > pi ? 2 * pi - angle : angle;

        float angleX = mapper.apply(xAxis2d.getRotation());
        float angleY = mapper.apply(yAxis2d.getRotation());
        float angleO = mapper.apply(offset2d.getRotation());

        // offset2d = u * xAxis2d + v * yAxis2d
        // use law of sines.
        float angleXY = pi - angleX - angleY;
        float angleXO = angleO + angleX;
        float angleYO = pi - angleXY - angleXO;
        float k = offset2d.length() / (float) Math.sin(angleXY);
        float u = k * (float) Math.sin(angleYO);
        float v = k * (float) Math.sin(angleXO);

        return new Vec2f(u, v);
    }

    public void flex(float xScale, float yScale) {
        xAxis.mul(xScale);
        yAxis.mul(yScale);
        xAxis2d.set(xAxis2d.x() * xScale, xAxis2d.y() * xScale);
        yAxis2d.set(yAxis2d.x() * yScale, yAxis2d.y() * yScale);
    }

    public void flex(float scale) {
        flex(scale, scale);
    }

    public void rot(float rad) {
        Vec2f xRot = xAxis2d.rotate(rad);
        Vec2f yRot = yAxis2d.rotate(rad);
        xAxis2d.set(xRot.x(), xRot.y());
        yAxis2d.set(yRot.x(), yRot.y());
        Vector3f neoXAxis = panel.map(xRot);
        Vector3f neoYAxis = panel.map(yRot);
        Vector3f offsetX = neoXAxis.copy();
        Vector3f offsetY = neoYAxis.copy();
        offsetX.sub(xAxis);
        offsetY.sub(yAxis);
        xAxis.add(offsetX);
        yAxis.add(offsetY);
    }

    public void rotDeg(float deg) {
        rot(VectorUtil.translateDegAndRad(deg, false));
    }

    public Ray getNormalRay(float x, float y) {
        Vector3f source = get(x, y);
        return new Ray(new Vector3f(panel.normal), source);
    }

    public @Nullable Vec2f rayToPoint(Ray ray) {
        Vector3f hitPoint = ray.getHitPoint(this.panel);
        if (!ray.pointOnRay(hitPoint)) return null;
        return get(hitPoint);
    }
}
