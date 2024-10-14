package kasuga.lib.core.model;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import java.util.LinkedList;
import java.util.List;

public interface Rotationable {
    Vector3f getPivot();
    Vector3f getRotation();
    boolean hasParent();
    Rotationable getParent();

    default RotationContext compileRotate(RotationContext context) {
        if (hasParent())
            context = getParent().compileRotate(context);
        Vector3f offset = this.getPivot().copy();
        offset.sub(context.lastPivot());
        context.quaternions.forEach(offset::transform);
        context.position.add(offset);

        if (!getRotation().equals(Vector3f.ZERO)) {
            Vector3f rotation = getRotation().copy();
            rotation.mul(-1, -1, 1);
            Quaternion cubeRot = Quaternion.ONE.copy();
            rotQuaternion(cubeRot, rotation);
            context.quaternions.add(0, cubeRot);
        }

        Vector3f pivot = this.getPivot().copy();
        return new RotationContext(context.position, pivot, context.quaternions);
    }

    default RotationContext startCompileRotate() {
        return this.compileRotate(
                new RotationContext(
                        Vector3f.ZERO.copy(),
                        Vector3f.ZERO.copy(),
                        new LinkedList<>()
                )
        );
    }

    default Vector3f getNearestValidPivot() {
        if (!this.getRotation().equals(Vector3f.ZERO)) return this.getPivot();
        if (this.hasParent()) return this.getParent().getNearestValidPivot();
        return Vector3f.ZERO;
    }

    private void rotQuaternion(Quaternion quaternion, Vector3f rotDeg) {
        quaternion.mul(Vector3f.ZP.rotationDegrees(rotDeg.z()));
        quaternion.mul(Vector3f.YP.rotationDegrees(rotDeg.y()));
        quaternion.mul(Vector3f.XP.rotationDegrees(rotDeg.x()));
    }


    record RotationContext(Vector3f position, Vector3f lastPivot, List<Quaternion> quaternions) {}
}
