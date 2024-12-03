package kasuga.lib.core.util.projectile;

import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class Ray {

    private final Vector3f source, forward;

    public Ray(final Vector3f source, final Vector3f forward) {
        this.source = source;
        this.forward = forward;
        forward.normalize();
    }

    public Ray(Vec3 source, Vec3 forward) {
        this(VectorUtil.vec3ToVec3f(source), VectorUtil.vec3ToVec3f(forward));
    }

    public void changeDirection(Quaternionf quaternion) {
        quaternion.transform(forward);
    }

    public void offset(final Vector3f offset) {
        source.add(offset);
    }

    public Vector3f get(float distance) {
        Vector3f result = new Vector3f(source);
        Vector3f f = new Vector3f(forward);
        f.mul(distance);
        result.add(f);
        return result;
    }

    public Ray reflect(final Vector3f normal, Vector3f neoSource) {
        Vec3 mir = VectorUtil.mirror(new Vec3(normal), new Vec3(forward));
        return new Ray(neoSource, VectorUtil.vec3ToVec3f(mir));
    }

    public Ray reflect(Panel panel) {
        if (panel.parallel(this.forward)) return this;
        if (panel.isPointOnPanel(this.source))
            return reflect(VectorUtil.vec3ToVec3f(panel.normal), this.source);
        Vector3f hitPoint = panel.getHitPoint(this);
        Vector3f test = new Vector3f(hitPoint);
        test.sub(source);
        if (test.dot(forward) < 0) return this;
        return reflect(VectorUtil.vec3ToVec3f(panel.normal), hitPoint);
    }

    public Vector3f getHitPoint(Panel panel) {
        return panel.getHitPoint(this);
    }

    public boolean canHit(Panel panel) {
        if (panel.isPointOnPanel(this.source)) return true;
        if (panel.parallel(this.forward)) return false;
        Vector3f hitPoint = panel.getHitPoint(this);
        Vector3f test = new Vector3f(hitPoint);
        test.sub(source);
        return test.dot(forward) >= 0;
    }

    public boolean pointOnRay(Vector3f vector) {
        Vector3f offset = new Vector3f(vector);
        offset.sub(source);
        Vector3f test = new Vector3f(offset);
        test.cross(forward);
        if (!test.equals(new Vector3f())) return false;
        return offset.dot(forward) >= 0;
    }
}