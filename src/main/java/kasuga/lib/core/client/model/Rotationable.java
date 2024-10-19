package kasuga.lib.core.client.model;


import com.mojang.math.Axis;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.List;

public interface Rotationable {

    static final Vector3f ZERO = new Vector3f();
    Vector3f getPivot();
    Vector3f getRotation();
    boolean hasParent();
    Rotationable getParent();

    default RotationContext compileRotate(RotationContext context) {
        if (hasParent())
            context = getParent().compileRotate(context);
        Vector3f offset = new Vector3f(this.getPivot());
        offset.sub(context.lastPivot());
        context.quaternions.forEach(quaternionf -> quaternionf.transform(offset));
        context.position.add(offset);

        if (!getRotation().equals(ZERO)) {
            Vector3f rotation = new Vector3f(getRotation());
            rotation.mul(-1, -1, 1);
            Quaternionf cubeRot = new Quaternionf();
            rotQuaternion(cubeRot, rotation);
            context.quaternions.add(0, cubeRot);
        }

        Vector3f pivot = new Vector3f(this.getPivot());
        return new RotationContext(context.position, pivot, context.quaternions);
    }

    default RotationContext startCompileRotate() {
        return this.compileRotate(
                new RotationContext(
                        new Vector3f(),
                        new Vector3f(),
                        new LinkedList<>()
                )
        );
    }

    default Vector3f getNearestValidPivot() {
        if (!this.getRotation().equals(ZERO)) return this.getPivot();
        if (this.hasParent()) return this.getParent().getNearestValidPivot();
        return new Vector3f(ZERO);
    }

    private void rotQuaternion(Quaternionf quaternion, Vector3f rotDeg) {
        quaternion.mul(Axis.ZP.rotationDegrees(rotDeg.z()));
        quaternion.mul(Axis.YP.rotationDegrees(rotDeg.y()));
        quaternion.mul(Axis.XP.rotationDegrees(rotDeg.x()));
    }


    record RotationContext(Vector3f position, Vector3f lastPivot, List<Quaternionf> quaternions) {}
}
