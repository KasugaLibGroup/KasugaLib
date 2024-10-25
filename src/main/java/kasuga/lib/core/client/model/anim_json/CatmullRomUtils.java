package kasuga.lib.core.client.model.anim_json;

import kasuga.lib.core.client.render.texture.Vec2f;
import org.joml.Vector3f;

public class CatmullRomUtils {
    public static final float DEFAULT_TAU = 0.5f;

    /**
     * Gen catmull-rom spline on times that you don't have p(i + 1)
     * @param p0 p(i - 2)
     * @param p1 p(i - 1)
     * @param p2 p(i)
     * @return (c0, c1, c2, c3)
     */
    public static Vector3f[] first3PointsToCRSPoints(Vector3f p0, Vector3f p1, Vector3f p2) {
        Vector3f p3 = new Vector3f(p2);
        p3.mul(2);
        p3.sub(p1);
        return genDefaultCRSPoints(p0, p1, p2, p3);
    }

    /**
     * Gen catmull-rom spline on times that you don't have p(i - 2)
     * @param p1 p(i - 1)
     * @param p2 p(i)
     * @param p3 p(i + 1)
     * @return (c0, c1, c2, c3)
     */
    public static Vector3f[] last3PointsToCRSPoints(Vector3f p1, Vector3f p2, Vector3f p3) {
        Vector3f p0 = new Vector3f(p1);
        p0.mul(2);
        p0.sub(p2);
        return genDefaultCRSPoints(p0, p1, p2, p3);
    }

    /**
     * Gen catmull-rom spline using tau = 0.5.
     * See {@link CatmullRomUtils#genCRSPoints(float, Vector3f, Vector3f, Vector3f, Vector3f)} for more info.
     * @param p0 p(i - 2)
     * @param p1 p(i - 1)
     * @param p2 p(i)
     * @param p3 p(i + 1)
     * @return (c0, c1, c2, c3)
     */
    public static Vector3f[] genDefaultCRSPoints
            (Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {
        return genCRSPoints(DEFAULT_TAU, p0, p1, p2, p3);
    }

    /**
     * Gen catmull-rom spline control vectors from your given points.
     * <p>
     * use this in {@link CatmullRomUtils#applyCRS(Vector3f[], float)}
     * <p>
     * Theoretical proof from
     * <a href="https://zhuanlan.zhihu.com/p/675073123">here</a>
     * @param tau the tau factor.
     * @param p0 p(i - 2)
     * @param p1 p(i - 1)
     * @param p2 p(i)
     * @param p3 p(i + 1)
     * @return (c0, c1, c2, c3)
     */
    public static Vector3f[] genCRSPoints
            (float tau, Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3) {
        // c0
        Vector3f c0 = new Vector3f(p1);

        // c1
        Vector3f c1 = new Vector3f(p0);
        c1.mul(-tau);
        Vector3f m1 = new Vector3f(p2);
        m1.mul(tau);
        c1.add(m1);

        // c2
        Vector3f c2 = new Vector3f(p0);
        c2.mul(2 * tau);
        Vector3f m2 = new Vector3f(p1);
        m2.mul(tau - 3);
        c2.add(m2);
        Vector3f m3 = new Vector3f(p2);
        m3.mul(3 - 2 * tau);
        c2.add(m3);
        Vector3f m4 = new Vector3f(p3);
        m4.mul(-tau);
        c2.add(m4);

        // c3
        Vector3f c3 = new Vector3f(p0);
        c3.mul(-tau);
        Vector3f m5 = new Vector3f(p1);
        m5.mul(2 - tau);
        c3.add(m5);
        Vector3f m6 = new Vector3f(p2);
        m6.mul(tau - 2);
        c3.add(m6);
        Vector3f m7 = new Vector3f(p3);
        m7.mul(tau);
        c3.add(m7);

        return new Vector3f[]{c0, c1, c2, c3};
    }

    /**
     * get catmull-rom spline result from given c-r vectors.
     * @param points points from {@link CatmullRomUtils#genCRSPoints(float, Vector3f, Vector3f, Vector3f, Vector3f)}
     * @param u the percentage from p(i - 1) to p(i), [0, 1]
     * @return result position
     */
    public static Vector3f applyCRS(Vector3f[] points, float u) {
        Vector3f result = new Vector3f(points[0]);
        Vector3f v1 = new Vector3f(points[1]);
        v1.mul(u);
        result.add(v1);
        Vector3f v2 = new Vector3f(points[2]);
        v2.mul(u * u);
        result.add(v2);
        Vector3f v3 = new Vector3f(points[3]);
        v3.mul(u * u * u);
        result.add(v3);
        return result;
    }

    public static Vec2f[] first3PointsToCRSPoints(Vec2f p0, Vec2f p1, Vec2f p2) {
        return genDefaultCRSPoints(p0, p1, p2, p2.scale(2).subtract(p1));
    }

    public static Vec2f[] last3PointsToCRSPoints(Vec2f p1, Vec2f p2, Vec2f p3) {
        return genDefaultCRSPoints(p1.scale(2).subtract(p2), p1, p2, p3);
    }

    public static Vec2f[] genDefaultCRSPoints
            (Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3) {
        return genCRSPoints(DEFAULT_TAU, p0, p1, p2, p3);
    }

    public static Vec2f[] genCRSPoints
            (float tau, Vec2f p0, Vec2f p1, Vec2f p2, Vec2f p3) {
        Vec2f c0 = p0;
        Vec2f c1 = p0.scale(-tau)
                .add(p2.scale(tau));
        Vec2f c2 = p0.scale(2 * tau)
                .add(p1.scale(tau - 3))
                .add(p2.scale(3 - 2 * tau))
                .add(p3.scale(-tau));
        Vec2f c3 = p0.scale(-tau)
                .add(p1.scale(2 - tau))
                .add(p2.scale(tau - 2))
                .add(p3.scale(tau));

        return new Vec2f[]{c0, c1, c2, c3};
    }

    public static Vec2f applyCRS(Vec2f[] points, float u) {
        return points[0]
                .add(points[1].scale(u))
                .add(points[2].scale(u * u))
                .add(points[3].scale(u * u * u));
    }
}
