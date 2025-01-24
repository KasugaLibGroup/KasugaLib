package kasuga.lib.core.util.projectile;

import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Function;

@Getter
public class Grid {

    private final Panel panel;

    private Vector3f xAxis, yAxis;

    private Vector3f o;

    private Vec2f xAxis2d, yAxis2d;

    public Grid(final Panel panel, Vector3f o, final Vec2f xAxis, final Vec2f yAxis) {
        this.panel = panel;
        this.o = o;
        this.xAxis2d = xAxis;
        this.yAxis2d = yAxis;
        this.xAxis = panel.map(xAxis);
        this.yAxis = panel.map(yAxis);
    }

    public Grid(Panel panel, Vector3f o) {
        this(panel, o, new Vec2f(1, 0), new Vec2f(0, 1));
    }

    private Grid(final Panel panel, Vector3f o, Vector3f xAxis, Vector3f yAxis, Vec2f xAxis2d, Vec2f yAxis2d) {
        this.panel = panel;
        this.o = o;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.xAxis2d = xAxis2d;
        this.yAxis2d = yAxis2d;
    }

    public void setO(Vector3f o) {
        this.o = o;
    }

    public Vector3f getO() {
        return o;
    }

    public Grid copy() {
        return new Grid(this.panel, this.o, this.xAxis, this.yAxis, xAxis2d, yAxis2d);
    }

    public Vector3f get(Vec2f vec) {
        return get(vec.x(), vec.y());
    }

    public Vector3f get(float x, float y) {
        Vector3f result = new Vector3f(this.o);
        Vector3f x3d = new Vector3f(xAxis);
        x3d.mul(x);
        result.add(x3d);
        Vector3f y3d = new Vector3f(yAxis);
        y3d.mul(y);
        result.add(y3d);
        return result;
    }

    public Vec2f get(Vector3f vec) {
        float pi = (float) Math.PI;
        Vector3f offset = new Vector3f(vec);
        offset.sub(this.o);
        Vec2f offset2d = panel.map(offset);
        Function<Float, Float> mapper =
                angle -> angle > pi ? 2 * pi - angle : angle;

        // use the law of sine.
        // vector a, b and c; ua + vb = c;
        float sina = vecSine(vec, yAxis);
        float sinb = vecSine(vec, xAxis);
        float sinc = vecSine(xAxis, yAxis);

        float cLen = len(vec);
        float bLen = cLen / sinc * sinb;
        float aLen = cLen / sinc * sina;
        return new Vec2f(aLen / this.xAxis2d.length(), bLen / this.yAxis2d.length());
    }

    public float vecSine(Vector3f vec1, Vector3f vec2) {
        Vector3f a = new Vector3f(vec1);
        a.normalize();
        Vector3f b = new Vector3f(vec2);
        b.normalize();
        a.cross(b);
        return len(a);
    }

    public float len(Vector3f vector3f) {
        return (float) Math.sqrt(vector3f.x() * vector3f.x() +
                vector3f.y() * vector3f.y() +
                vector3f.z() * vector3f.z());
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
        xAxis2d = xAxis2d.rotate(rad);
        yAxis2d = yAxis2d.rotate(rad);

        xAxis = panel.map(xAxis2d);
        yAxis = panel.map(yAxis2d);
    }

    public void rotDeg(float deg) {
        rot(VectorUtil.translateDegAndRad(deg, false));
    }

    public Ray getNormalRay(float x, float y) {
        Vector3f source = get(x, y);
        return new Ray(source, new Vector3f(panel.normal));
    }

    public Ray getNormalRay(Vec2f pos) {
        return getNormalRay(pos.x(), pos.y());
    }

    public @Nullable Vec2f rayToPoint(Ray ray) {
        Vector3f hitPoint = ray.getHitPoint(this.panel);
        if (!ray.pointOnRay(hitPoint)) return null;
        return get(hitPoint);
    }

    @Override
    public String toString() {
        return "Grid<\n    " + panel + ", \n    " +
               o + ", \n    " + xAxis2d + ", " + yAxis2d + ",\n    "
                + xAxis + ", " + yAxis + "\n>";
    }

    public static void main(String[] args) {
        Vec3 normal = new Vec3(1, 1, 1);
        Panel panel = new Panel(Vec3.ZERO, normal);
        Vec2f xo2f = new Vec2f(1, 0);
        Vec2f yo2f = new Vec2f(0, 1);
        Grid grid = new Grid(panel, new Vector3f(), xo2f, yo2f);
        grid.rotDeg(80);
        Vector3f vector3f = grid.get(new Vec2f(0.5f, 0.5f));
        Vec2f vec2f = grid.get(vector3f);
        System.out.println(grid);
        System.out.println(vector3f);
        System.out.println(vec2f);
    }
}
