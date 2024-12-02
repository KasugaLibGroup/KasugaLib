package kasuga.lib.core.util.projectile;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import net.minecraft.world.phys.Vec3;

@Getter
public class Ray {

    private final Vector3f source, forward;

    public Ray(final Vector3f source, final Vector3f forward) {
        this.source = source;
        this.forward = forward;
        forward.normalize();
    }

    public Ray(Vec3 source, Vec3 forward) {
        this(new Vector3f(source), new Vector3f(forward));
    }

    public void changeDirection(Quaternion quaternion) {
        forward.transform(quaternion);
    }

    public void offset(final Vector3f offset) {
        source.add(offset);
    }

    public Vector3f get(float distance) {
        Vector3f result = source.copy();
        Vector3f f = forward.copy();
        f.mul(distance);
        result.add(f);
        return result;
    }

    public Ray reflect(final Vector3f normal, Vector3f neoSource) {
        Vec3 mir = VectorUtil.mirror(new Vec3(normal), new Vec3(forward));
        return new Ray(neoSource, new Vector3f(mir));
    }

    public Ray reflect(Panel panel) {
        if (panel.parallel(this.forward)) return this;
        if (panel.isPointOnPanel(this.source))
            return reflect(new Vector3f(panel.normal), this.source);
        Vector3f hitPoint = panel.getHitPoint(this);
        Vector3f test = hitPoint.copy();
        test.sub(source);
        if (test.dot(forward) < 0) return this;
        return reflect(new Vector3f(panel.normal), hitPoint);
    }

    public Vector3f getHitPoint(Panel panel) {
        return panel.getHitPoint(this);
    }

    public boolean canHit(Panel panel) {
        if (panel.isPointOnPanel(this.source)) return true;
        if (panel.parallel(this.forward)) return false;
        Vector3f hitPoint = panel.getHitPoint(this);
        Vector3f test = hitPoint.copy();
        test.sub(source);
        return test.dot(forward) >= 0;
    }

    public boolean pointOnRay(Vector3f vector) {
        Vector3f offset = vector.copy();
        offset.sub(source);
        Vector3f test = offset.copy();
        test.cross(forward);
        if (!test.equals(Vector3f.ZERO)) return false;
        return offset.dot(forward) >= 0;
    }
}