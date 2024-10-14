package kasuga.lib.core.model.anim_json;

import com.mojang.math.Vector3f;

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
        Vector3f p3 = p2.copy();
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
        Vector3f p0 = p1.copy();
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
        Vector3f c0 = p1.copy();

        // c1
        Vector3f c1 = p0.copy();
        c1.mul(-tau);
        Vector3f m1 = p2.copy();
        m1.mul(tau);
        c1.add(m1);

        // c2
        Vector3f c2 = p0.copy();
        c2.mul(2 * tau);
        Vector3f m2 = p1.copy();
        m2.mul(tau - 3);
        c2.add(m2);
        Vector3f m3 = p2.copy();
        m3.mul(3 - 2 * tau);
        c2.add(m3);
        Vector3f m4 = p3.copy();
        m4.mul(-tau);
        c2.add(m4);

        // c3
        Vector3f c3 = p0.copy();
        c3.mul(-tau);
        Vector3f m5 = p1.copy();
        m5.mul(2 - tau);
        c3.add(m5);
        Vector3f m6 = p2.copy();
        m6.mul(tau - 2);
        c3.add(m6);
        Vector3f m7 = p3.copy();
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
        Vector3f result = points[0].copy();
        Vector3f v1 = points[1].copy();
        v1.mul(u);
        result.add(v1);
        Vector3f v2 = points[2].copy();
        v2.mul(u * u);
        result.add(v2);
        Vector3f v3 = points[3].copy();
        v3.mul(u * u * u);
        result.add(v3);
        return result;
    }
}
