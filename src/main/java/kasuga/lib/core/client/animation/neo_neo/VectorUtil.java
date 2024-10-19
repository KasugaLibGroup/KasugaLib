package kasuga.lib.core.client.animation.neo_neo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kasuga.lib.core.annos.Util;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;


@Util
public class VectorUtil {

    public static Vec3 rot(Axis axis, Vec3 org, float rot, boolean degree) {
        Vector3f org3f = VectorUtil.vec3ToVec3f(org);
        Quaternionf quaternionf = degree ? fromXYZDegrees(org3f) : fromXYZ(org3f);
        quaternionf.mul(degree ? axis.rotationDegrees(rot) : axis.rotation(rot));
        return VectorUtil.vec3fToVec3(degree ? toXYZDegrees(quaternionf) : toXYZ(quaternionf));
    }

    public static Quaternionf rotQuaternionf(Quaternionf org, Quaternionf rot) {
        Quaternionf quaternionf = new Quaternionf(org);
        quaternionf.mul(rot);
        return quaternionf;
    }

    public static Vec3 rot(Vec3 org, Vec3 rot, boolean degree) {
        Vector3f org3f = VectorUtil.vec3ToVec3f(org),
                rot3f = VectorUtil.vec3ToVec3f(rot);
        Quaternionf orgQ = degree ? fromXYZDegrees(org3f) : fromXYZ(org3f),
                rotQ = degree ? fromXYZDegrees(rot3f) : fromXYZ(rot3f);
        orgQ.mul(rotQ);
        return VectorUtil.vec3fToVec3(degree ? toXYZDegrees(orgQ) : toXYZ(orgQ));
    }

    public static Quaternionf getQuaternionf(Vec3 rot, boolean degree) {
        Vector3f vector3f = VectorUtil.vec3ToVec3f(rot);
        return degree ? fromXYZDegrees(vector3f) : fromXYZ(vector3f);
    }


    public static Vec3 degToRad(Vec3 deg) {
        return deg.scale(Math.PI / 180);
    }

    public static Vec3 radToDeg(Vec3 rad) {
        return rad.scale(180 / Math.PI);
    }

    public static Vec3 rotX(Vec3 org, float rot, boolean degree) {
        return rot(Axis.XP, org, rot, degree);
    }

    public static Vec3 rotY(Vec3 org, float rot, boolean degree) {
        return rot(Axis.YP, org, rot, degree);
    }

    public static Vec3 rotZ(Vec3 org, float rot, boolean degree) {
        return rot(Axis.ZP, org, rot, degree);
    }

    public static Vec3 rotXDeg(Vec3 org, float rot) {
        return rotX(org, rot, true);
    }

    public static Vec3 rotYDeg(Vec3 org, float rot) {
        return rotY(org, rot, true);
    }

    public static Vec3 rotZDeg(Vec3 org, float rot) {
        return rotZ(org, rot, true);
    }

    public static Vec3 rotXRad(Vec3 org, float rot) {
        return rotX(org, rot, false);
    }

    public static Vec3 rotYRad(Vec3 org, float rot) {
        return rotY(org, rot, false);
    }

    public static Vec3 rotZRad(Vec3 org, float rot) {
        return rotZ(org, rot, false);
    }



    public static void translate(PoseStack pose, Vec3 vec3) {
        pose.translate(vec3.x(), vec3.y(), vec3.z());
    }

    public static void rot(PoseStack pose, Vec3 rotation, boolean degree) {
        pose.mulPose(getQuaternionf(rotation, degree));
    }

    public static float translateDegAndRad(float rot, boolean toDeg) {
        return toDeg ? (rot * 180f / (float) Math.PI) : (rot * (float) Math.PI / 180f);
    }

    public static Vec3 translateDegAndRad(Vec3 rot, boolean toDeg) {
        return toDeg ? rot.scale(180f / Math.PI) : rot.scale(Math.PI / 180f);
    }

    public static void scale(PoseStack pose, Vec3 scale) {
        pose.scale((float) scale.x(), (float) scale.y(), (float) scale.z());
    }

    public static Vec3 normalize(Vec3 vec3) {
        return vec3.scale(1 / vec3.length());
    }

    public static double cosVector(Vec3 vec1, Vec3 vec2) {
        double factor1 = vec1.dot(vec2);
        double factor2 = Math.sqrt(vec2.lengthSqr() * vec1.lengthSqr());
        return factor1 / factor2;
    }

    /**
     * Calculate the right triangle perpendicular vectors of vector 1 and vector 2.
     * @param vec1 hypotenuse.
     * @param vec2 Right angle side 1 of triangle.
     * @return Right angle side 2 of triangle.
     */
    public static Vec3 normalBetween(Vec3 vec1, Vec3 vec2) {
        double cosine = cosVector(vec1, vec2);
        double scale = vec1.length() * cosine;
        Vec3 vec = vec2.scale(scale * vec1.length() / vec2.length());
        return vec1.subtract(vec);
    }

    public static Vec3 mirror(Vec3 axis, Vec3 vector) {
        if (axis.dot(vector) == 0) return vector;
        Vec3 normal = normalBetween(vector, axis);
        return vector.add(normal.scale(2));
    }

    public static Vector3f vec3ToVec3f(Vec3 vec3) {
        return vec3.toVector3f();
    }

    public static Vec3 vec3fToVec3(Vector3f vector3f) {
        return new Vec3(vector3f);
    }

    public static Quaternionf fromXYZ(Vector3f rotations) {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateXYZ(rotations.x(), rotations.y(), rotations.z());
        return quaternionf;
    }

    public static Quaternionf fromXYZ(float x, float y, float z) {
        return fromXYZ(new Vector3f(x, y, z));
    }

    public static Quaternionf fromXYZDegrees(Vector3f rotations) {
        Vector3f rad = new Vector3f(rotations);
        rad.mul((float) Math.PI / 180f);
        return fromXYZ(rad);
    }

    public static Vector3f toXYZ(Quaternionf quaternionf) {
        float f = quaternionf.w() * quaternionf.w();
        float f1 = quaternionf.x() * quaternionf.x();
        float f2 = quaternionf.y() * quaternionf.y();
        float f3 = quaternionf.z() * quaternionf.z();
        float f4 = f + f1 + f2 + f3;
        float f5 = 2.0F * quaternionf.w() * quaternionf.x() - 2.0F * quaternionf.y() * quaternionf.z();
        float f6 = (float)Math.asin((double)(f5 / f4));
        return Math.abs(f5) > 0.999F * f4 ? new Vector3f(2.0F * (float)Math.atan2((double)quaternionf.x(), (double)quaternionf.w()), f6, 0.0F) : new Vector3f((float)Math.atan2((double)(2.0F * quaternionf.y() * quaternionf.z() + 2.0F * quaternionf.x() * quaternionf.w()), (double)(f - f1 - f2 + f3)), f6, (float)Math.atan2((double)(2.0F * quaternionf.x() * quaternionf.y() + 2.0F * quaternionf.w() * quaternionf.z()), (double)(f + f1 - f2 - f3)));
    }

    public static Vector3f toXYZDegrees(Quaternionf quaternionf) {
        Vector3f vector3f = toXYZ(quaternionf);
        return new Vector3f((float)Math.toDegrees((double)vector3f.x()), (float)Math.toDegrees((double)vector3f.y()), (float)Math.toDegrees((double)vector3f.z()));
    }
    public static JsonArray vec3fToJsonArray(Vector3f vector3f) {
        JsonArray array = new JsonArray(3);
        array.add(vector3f.x());
        array.add(vector3f.y());
        array.add(vector3f.z());
        return array;
    }
}
