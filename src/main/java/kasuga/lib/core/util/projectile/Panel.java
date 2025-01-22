package kasuga.lib.core.util.projectile;

import com.mojang.math.*;
import kasuga.lib.core.client.animation.neo_neo.VectorIOUtil;
import kasuga.lib.core.client.animation.neo_neo.VectorUtil;
import kasuga.lib.core.client.render.texture.Vec2f;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Stack;

@Getter
public class Panel {

    public static PanelRenderer test = null;

    // Ax + By + Cz = constant
    // constant = Ax0+ By0 + Cy0
    @Setter
    public Vec3 normal;

    public double constant;

    public Panel(Panel panel) {
        this.normal = panel.normal;
        this.constant = panel.constant;
    }

    private Panel(Vec3 normal, double constant) {
        this.normal = normal;
        this.constant = constant;
    }

    public Panel(Vector3f pos1, Vector3f pos2, Vector3f pos3) {
        this(new Vec3(pos3), new Vec3(pos1).cross(new Vec3(pos2)));
    }

    public Panel(Vector3f point, Vector3f normal) {
        this.normal = new Vec3(normal);
        this.constant = point.dot(normal);
    }

    public Panel(Vec3 point, Vec3 normal) {
        this.normal = normal;
        this.constant = point.dot(normal);
    }

    public Panel(Vec3 pos1, Vec3 pos2, Vec3 pos3) {
        this(pos3, pos1.cross(pos2));
    }

    public Panel copy() {
        return new Panel(this);
    }

    public Panel moveAndCopy(Vec3 point) {
        return new Panel(this.normal, point);
    }

    public void moveTo(Vec3 point) {
        this.constant = point.dot(normal);
    }

    public Panel offset(Vec3 offset) {
        double offsetConst = offset.dot(normal);
        return new Panel(this.normal.add(offset), this.constant + offsetConst);
    }

    public Panel rotate(Quaternion quaternion, Vec3 referencePoint) {
        assert isPointOnPanel(referencePoint);
        Vector3f neoNormal = new Vector3f(normal);
        neoNormal.transform(quaternion);
        return new Panel(referencePoint, new Vec3(neoNormal));
    }

    public Panel rotateDeg(Vector3f rotationDeg, Vec3 referencePoint) {
        return rotate(Quaternion.fromXYZDegrees(rotationDeg), referencePoint);
    }

    public Panel rotate(Vector3f rotation, Vec3 referencePoint) {
        return rotate(Quaternion.fromXYZ(rotation), referencePoint);
    }

    public Panel transform(Matrix4f matrix) {
        Vector4f vector4f = new Vector4f((float) this.normal.x(),
                (float) this.normal.y(),
                (float) this.normal.z(),
                (float) this.constant);
        vector4f.transform(matrix);
        return new Panel(new Vec3(vector4f.x(), vector4f.y(), vector4f.z()), vector4f.w());
    }

    public Panel reverse() {
        return new Panel(this.normal.reverse(), this.constant);
    }

    public Vector3f getNormal() {
        return new Vector3f(normal);
    }

    public boolean isPointOnPanel(Vec3 point) {
        return normal.dot(point) == constant;
    }

    public boolean isPointOnPanel(Vector3f point) {
        return isPointOnPanel(new Vec3(point));
    }

    public boolean isVecHitPanel(Vec3 vec) {
        return vec.dot(normal) == 0;
    }

    public boolean isVecHitPanel(Vector3f vec) {
        return isVecHitPanel(new Vec3(vec));
    }

    public Vec3 getHitPoint(Vec3 pos, Vec3 forward) {
        if (isPointOnPanel(pos)) return pos;
        assert valid();
        assert isVecHitPanel(forward);
        Vec3 referencePoint = defaultReferencePoint();
        double d = referencePoint.subtract(pos).dot(normal)
                / forward.dot(normal);
        return forward.scale(d).add(pos);
    }

    public Vector3f getHitPoint(Vector3f pos, Vector3f forward) {
        return new Vector3f(getHitPoint(new Vec3(pos), new Vec3(forward)));
    }

    public Vector3f getHitPoint(Ray ray) {
        return getHitPoint(ray.getSource(), ray.getForward());
    }

    public Vec3 defaultReferencePoint() {
        assert valid();
        if (parallelWithXOZ()) {
            return new Vec3(1, getY(1, 1), 1);
        } else if (parallelWithYOZ()) {
            return new Vec3(getX(1, 1), 1, 1);
        } else {
            return new Vec3(1 ,1, getZ(1, 1));
        }
    }

    public boolean valid() {
        return !normal.equals(Vec3.ZERO);
    }

    public boolean parallelWith(Vec3 normal) {
        return this.normal.cross(normal).equals(Vec3.ZERO);
    }

    public boolean parallel(Vec3 vec3) {
        return this.normal.dot(vec3) == 0;
    }

    public boolean parallel(Vector3f vector3f) {
        return this.normal.dot(new Vec3(vector3f)) == 0;
    }

    public boolean parallelWithXOY() {
        return parallelWith(new Vec3(0, 0, 1));
    }

    public boolean parallelWithXOZ() {
        return parallelWith(new Vec3(0, 1, 0));
    }

    public boolean parallelWithYOZ() {
        return parallelWith(new Vec3(1, 0, 0));
    }

    public Vec3 yAxisIntersection() {
        return new Vec3(0, getY(0, 0), 0);
    }

    public Vec3 xAxisIntersection() {
        return new Vec3(getX(0, 0), 0, 0);
    }

    public Vec3 zAxisIntersection() {
        return new Vec3(0, 0, getZ(0, 0));
    }

    public double getX(double y, double z) {
        return (constant - normal.y() * y - normal.z() * z) / normal.x();
    }

    public double getY(double x, double z) {
        return (constant - normal.x() * x - normal.z() * z) / normal.y();
    }

    public double getZ(double x, double y) {
        return (constant - normal.x() * x - normal.y() * y) / normal.z();
    }

    public Vector3f map(Vec2f vec) {
        Vec2f pitchAndYaw = getPitchAndYaw(this.normal);
        Quaternion quaternion = Quaternion.fromXYZ(0, pitchAndYaw.y(), pitchAndYaw.x());
        Vector3f vector3f = new Vector3f(vec.x(), 0, vec.y());
        vector3f.transform(quaternion);
        return vector3f;
    }

    public Vec2f map(Vector3f vec) {
        assert parallel(vec);
        Vec2f pitchAndYaw = getPitchAndYaw(this.normal).invert();
        Quaternion quaternion = Quaternion.fromXYZ(0, pitchAndYaw.y(), pitchAndYaw.x());
        Vector3f vec3f = vec.copy();
        vec3f.transform(quaternion);
        return new Vec2f(vec3f.x(), vec3f.z());
    }

    public static Vec2f getPitchAndYaw(Vec3 vec) {
        float pi = (float) Math.PI;
        Vec2f horizontal = new Vec2f((float) vec.x(), (float) vec.z());
        if (horizontal.lengthSqr() == 0) {
            return new Vec2f(vec.y() >= 0 ? 0 : pi, 0);
        }

        float yaw = horizontal.getRotation();
        float vecLen = (float) vec.length();

        float pitch = vecLen == 0 ? 0 : (float) Math.asin(horizontal.length() / vecLen);
        if (vec.y() < 0) pitch = pi - pitch;
        return new Vec2f(- pitch, yaw);
    }

    public Quaternion getQuaternion() {
        return getQuaternion(this.normal);
    }

    public static Quaternion getQuaternion(Vec3 vec) {
        Vec2f pitchAndYaw = getPitchAndYaw(vec);
        Quaternion result = Quaternion.ONE.copy();
        Quaternion yaw = Quaternion.fromXYZ(0, pitchAndYaw.y(), 0);
        Quaternion pitch = Quaternion.fromXYZ(0, 0, pitchAndYaw.x());
        result.mul(yaw);
        result.mul(pitch);
        return result;
    }

    public static Quaternion getQuaternion(Vector3f vector3f) {
        return getQuaternion(new Vec3(vector3f));
    }

    public Vector3f getRotationVector() {
        return getQuaternion().toXYZ();
    }

    public Vector3f getRotationDegVector() {
        return getQuaternion().toXYZDegrees();
    }

    @Override
    public String toString() {
        return "Panel<norm: " + normal.toString() + ", constant: " + constant + ">";
    }

    public static void main(String[] args) {
        Vec3 norm = new Vec3(0, 1, 0);
        Vec3 pos = Vec3.ZERO;
        Panel panel = new Panel(pos, norm);
        panel = panel.rotateDeg(new Vector3f(0, 0, 90), pos);
        System.out.println(panel);
        System.out.println(panel.getRotationDegVector());
    }
}
